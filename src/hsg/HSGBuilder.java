package hsg;

import Database.Graph.Node;
import events.ttps.TTP;
import provenanceGraph.ProvGraph;

import java.util.*;

/**
 * Stellt den HSG nach außen dar.
 * Kann erzeugt und ausgegeben werden, sonst noch nicht so viel
 */
public class HSGBuilder {
    private final ProvGraph graph;
    private final PathFactorEngine pfEngine;

    private final Map<String, Set<String>> hsgEdges = new HashMap<>();
    private final Map<String, Node> hsgNodes = new HashMap<>();

    public HSGBuilder(ProvGraph graph, PathFactorEngine pfEngine){
        this.graph = graph;
        this.pfEngine = pfEngine;
    }

    /**
     * Sammelt alle Knoten, an denen TTPs gefunden wurden.
     * Verbindet sie unter Berücksichtigung des PathFactors.
     * Gibt Verbindungen auf Konsole aus.
     */
    public void constructHSG(){
        collectTTPNodes();
        connectNodes();
        printHSG();
    }

    /**
     * Sammelt alle ProvGraph Knoten mit gefundenen TTPs
     * in der hsgNodes Map und erstellt zu befüllende Kanten
     */
    private void collectTTPNodes() {
        for(Node n : graph.getNodes().values()){
            //Nur Knoten mit TTPs
            if(!n.getTtps().isEmpty()){
                hsgNodes.put(n.getHashId(), n);
                hsgEdges.put(n.getHashId(), new HashSet<>());
            }
        }
        System.out.println("[HSG] "+ hsgNodes.size()+ " potenzielle Knoten gefunden");
    }

    /**
     * Verbindet die Knoten in hsgNodes miteinander,
     * wenn der PathFactor stimmt
     */
    private void connectNodes(){
        List<String> nodeIds = new ArrayList<>(hsgNodes.keySet());
        //Für jeden Quellknoten
        for(int i =0; i< nodeIds.size(); i++){
            String srcId = nodeIds.get(i);
            Map<String,Integer> pathFactors = pfEngine.computePfFrom(srcId);

            //Für jeden Zielknoten
            for (int j=0; j<nodeIds.size(); j++){
                if(i!=j){
                    String dstId= nodeIds.get(j);
                    int pf = pathFactors.getOrDefault(dstId, Integer.MAX_VALUE);
                    //Wenn PF passt Kante erzeugen
                    if(pf <= TTP.PF_THRESHOLD){
                        hsgEdges.get(srcId).add(dstId);
                    }
                }
            }
        }
    }


    /**
     * Gibt den HSG auf der Konsole aus
     * Gibt es Möglichkeiten den richtig krass darzustellen?
     * -> Wenn Zeit nachschauen
     */
    private void printHSG(){
        for(Map.Entry<String, Set<String>> entry : hsgEdges.entrySet()){
            Node srcNode = hsgNodes.get(entry.getKey());
            if(entry.getValue().isEmpty()){
                System.out.println(printNode(srcNode)+ "---> [LEER]");
                continue;
            }
            for (String dstId : entry.getValue()){
                Node dstNode = hsgNodes.get(dstId);
                System.out.println(printNode(srcNode) + "-->"+ printNode(dstNode));
            }

        }
    }

    //Hilfsmethode für einheitliche Knoten ausgabe
    private String printNode(Node node){
        return "[" +node.getName() + " ("+ node.getTtps()+ ")]";
    }
}
