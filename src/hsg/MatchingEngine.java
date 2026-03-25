package hsg;

import Database.Graph.Edge;
import Database.Graph.Node;
import Database.Graph.Subject;
import events.EventType;
import events.ttps.TTP;
import provenanceGraph.ProvGraph;

import java.util.*;

/**
 * Soll Einhaltung der TTPs prüfen und entsprechende Schritte einleiten
 * Soll TTP.matches(edge) aufrufen und ggf. HSG-Knoten-Erstellung anfordern
 */
public class MatchingEngine {
    private final ProvGraph graph;

    public MatchingEngine(ProvGraph graph){
        this.graph = graph;
    }

    /**
     * Sucht nach Initial_Compromise.
     * Verfolgt Kette an zusammenhängenden Ereignissen (unter Berücksichtigung PF).
     * Hält aufeinanderfolgende TTPs in TTPChains fest
     * @param phases Zu suchende TTPs jeweils in Listen nach Phase
     */
    public void matchTTPs(List<List<TTP>> phases){
        //Initial_Compromise finden
        for(Edge e: graph.getEdges()){
            for(TTP ttp: phases.get(0)){ //initial_compromise1
                if(ttp.matches(e,graph)){
                    Node match = e.getDstNode();
                    //Initial_Compromise entdeckt -> neue Kette starten
                    TTPChain newChain = new TTPChain(ttp.getName(), match.getHashId());
                    match.addChain(newChain);
                    match.addTTP(ttp);
                    System.out.println("[NEW CHAIN] "+ ttp.getName()+ " auf "+ match.getName());

                }
            }
        }

        //Kette verfolgen und auf spätere Phasen testen
        for(Node startNode: graph.getNodes().values()){
            if(!startNode.getChains().isEmpty()){
                //Vom Startknoten zu erreichende Knoten durchlaufen
                Queue<String> queue = new LinkedList<>();
                Map<String, Integer> visitedPF = new HashMap<>();
                queue.add(startNode.getHashId());
                visitedPF.put(startNode.getHashId(),1);

                while(!queue.isEmpty()){
                    String currentId = queue.poll();
                    Node currentNode = graph.getNode(currentId);
                    int currentPF = visitedPF.get(currentId);

                    for(Edge e: graph.getOutEdges(currentId)){
                        Node dstNode = e.getDstNode();
                        String dstId = dstNode.getHashId();
                        //Neuen PF bestimmen
                        int newPF = computeNewPF(currentNode,dstNode, currentPF);
                        //Wenn PF>Threshold, wird Kette abgebrochen
                        if(newPF <= TTP.PF_THRESHOLD){
                            //TTP Matching
                            for(List<TTP> phase : phases){
                                for(TTP ttp: phase){
                                    if(ttp.matches(e, graph)){

                                        for(TTPChain chain: currentNode.getChains()){
                                            if(!chain.getTtps().contains(ttp.getName())){
                                                //Kette erweitern
                                                TTPChain extend = chain.extendChain(ttp.getName(), newPF);
                                                //Nur wenn (inhaltlich) gleiche Chain noch nicht existiert
                                                if(!dstNode.hasChain(extend)) {
                                                    dstNode.addChain(extend);
                                                    dstNode.addTTP(ttp);
                                                    System.out.println("[CHAIN EXTENDED] " + extend + " auf " + dstNode.getName());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            //Auch ohne Match weiter traversieren
                            if(!visitedPF.containsKey(dstId) || visitedPF.get(dstId) > newPF){
                                visitedPF.put(dstId,newPF);
                                queue.add(dstId);
                            }
                        }
                    }
                }


            }
        }
    }

    /**
     * Berechnung einens neuen PF
     * @param srcNode Ursprungsknoten
     * @param dstNode Zielknoten
     * @param currentPF aktueller PF
     * @return currentPF++, wenn nötig. Sonst currentPF
     */
    private int computeNewPF(Node srcNode, Node dstNode, int currentPF){
        if(!(dstNode instanceof Subject)){
            return currentPF;
        }
        for(Edge e: graph.getInEdges(dstNode.getHashId())){
            if(e.getOperation().equals(EventType.Type.EVENT_FORK.toString()) && e.getSrcNode().getHashId().equals((srcNode.getHashId()))){
                return currentPF;
            }
        }
        return currentPF +1;
    }

}
