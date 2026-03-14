package hsg;

import Database.Graph.Edge;
import Database.Graph.Node;
import Database.Graph.Subject;
import ProvenanceGraph.ProvGraph;

import java.util.*;

public class PathFactorEngine {
    private final Node origin;

    private final Map<String, Integer> pathFactors = new HashMap<>();

    private final Map<String, Set<String>> ancestorSets = new HashMap<>();

    private final ProvGraph graph;

    public PathFactorEngine(Node origin, ProvGraph graph){
        this.origin = origin;
        this.graph = graph;
        //Startknoten hat PF = 1
        String originId = origin.getHashId();
        pathFactors.put(originId, 1);

        // Origin ist sein eigener Vorfahre(für Vergleiche)
        Set<String> origAncestors = new HashSet<>();
        origAncestors.add(originId);
        ancestorSets.put(originId, origAncestors);
    }

    public void processEdge(Edge edge){
        Node src = edge.getSrcNode();
        Node dst = edge.getDstNode();
        String srcId = src.getHashId();
        String dstId = dst.getHashId();

        //Nur, wenn src von Origin erreichbar ist
        if(pathFactors.containsKey(srcId)){
            int srcPF = pathFactors.get(srcId);
            int newPF;

            // PF bleibt gleich, wenn dst kein Prozess-Knoten ist
            if(!(dst instanceof Subject)){
                newPF = srcPF;
            } else { //Knoten ist Prozess
                Subject dstSubject = (Subject) dst;
                if(sharesAncestor(srcId, dstId)){
                    newPF = srcPF;
                } else{ //Kein gemeinsamer Vorfahre -> PF++
                    newPF = srcPF+1;
                }
                updateAncestors(srcId, dstId);
            }
            //Minimum speichern
            if(!pathFactors.containsKey(dstId) || newPF<pathFactors.get(dstId)){
                pathFactors.put(dstId, newPF);
            }
        }
    }

    /**
     * Liefert den berechneten PF des Knotens
     * @param nodeHashId ID des Knotens
     * @return pathFactor oder Integer.MAX_VALUE, wenn nicht möglich
     */
    public int getPathFactor(String nodeHashId){
        return pathFactors.getOrDefault(nodeHashId,Integer.MAX_VALUE);
    }

    /*
     * Prüft, ob die Knoten src und dst (mindestens) einen gemeinsamen Vorfahren haben
     */
    private boolean sharesAncestor(String srcId, String dstId) {
        Set<String> srcAncestors = ancestorSets.getOrDefault(srcId, Collections.emptySet());
        Set<String> dstAncestors = ancestorSets.getOrDefault(dstId, Collections.emptySet());

        for(String ancestor : srcAncestors){
            if(dstAncestors.contains(ancestor)){
                //Wenn Knoten sowohl Vorfahre von dst als auch von src ist
                return true;
            }
        }
        return false;
    }

    /*
    Erweitert AncestorSet von dst.
    Erbt Vorfahren von src und fügt sich selbst hinzu
     */
    private void updateAncestors(String srcId, String dstId){
        Set<String> newAncestors = new HashSet<>();
        //Vorfahren von src erben & sich selbst hinzufügen
        newAncestors.addAll(ancestorSets.getOrDefault(srcId, Collections.emptySet()));
        newAncestors.add(dstId);

        if(ancestorSets.containsKey(dstId)){
            //falls dst schon eigene Vorfahren hat, wird newAncestors denen hinzugefügt
            ancestorSets.get(dstId).addAll(newAncestors);
        } else{
            ancestorSets.put(dstId, newAncestors);
        }

    }

    public Node getOrigin(){
        return origin;
    }
}
