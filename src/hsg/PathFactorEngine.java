package hsg;

import Database.Graph.Edge;
import Database.Graph.Node;
import Database.Graph.Subject;
import provenanceGraph.ProvGraph;

import java.util.*;

/**
 * VERALTET
 * WIRD (stand jetzt) NICHT VERWENDET.
 * Komplexere PF-Berechnung mit caching, aber nicht effizient
 *
 * Klasse für PathFactor-Aufgaben
 * "Online"-Version, wenn stetig neue Kanten kommen vllt deutlich aufwändiger (erweitern, wenn Zeit?)
 */
public class PathFactorEngine {
    private final ProvGraph graph;

    private final Map<String, Map<String,Integer>> cache = new HashMap<>();

    public PathFactorEngine(ProvGraph graph) {
        this.graph = graph;
    }

    /**
     * Berechnet alle PFs vom Ursprungsknoten aus
     * @param origId ID des Ursprungs (wo TTP gematcht wurde)
     * @return Map hashId -> PF
     */
    public Map<String, Integer> computePfFrom(String origId) {
        //direkt zurückgeben, wenn im cache (spart super viele Berechnungen)
        if(cache.containsKey(origId)){
            return cache.get(origId);
        }
        Map<String, Integer> pathFactors = new HashMap<>();
        Map<String, Set<String>> ancestorSets = new HashMap<>();

        //Startknoten hat PF=  1
        pathFactors.put(origId, 1);
        Set<String> origAncestors = new HashSet<>();
        origAncestors.add(origId); //ist sein eigener Vorfahre(für Vergleiche)
        ancestorSets.put(origId, origAncestors);

        Queue<String> queue = new LinkedList<>();
        queue.add(origId);
        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            int currentPF = pathFactors.get(currentId);

            for (Edge e : graph.getOutEdges(currentId)) {
                Node dst = e.getDstNode();
                String dstId = dst.getHashId();

                int newPF;
                if (!(dst instanceof Subject)) {
                    //Wenn dst kein Prozess-Knoten, bleibt PF gleich
                    newPF = currentPF;
                } else {
                    Set<String> currentAncestors = ancestorSets.getOrDefault(currentId, Collections.emptySet());
                    Set<String> dstAncestors = ancestorSets.getOrDefault(dstId, Collections.emptySet());
                    //Haben beide Knoten gemeinsame Vorfahren?
                    boolean shareAncestor = !Collections.disjoint(currentAncestors, dstAncestors);
                    if (shareAncestor) {
                        newPF = currentPF;
                    } else { //kein gemeinsamer Vorfahre -> PF++
                        newPF = currentPF + 1;
                    }
                }
                //AncestorSet von dst erweitern
                Set<String> newAncestors = new HashSet<>(ancestorSets.getOrDefault(currentId, Collections.emptySet()));
                newAncestors.add(dstId);
                if(ancestorSets.containsKey(dstId)){
                    //falls dst schon eigene Vorfahren hat, wird newAncestors denen hinzugefügt
                    ancestorSets.get(dstId).addAll(newAncestors);
                } else{
                    ancestorSets.put(dstId, newAncestors);
                }
                //Nur anpassen, wenn newPF < bisher
                if (!pathFactors.containsKey(dstId) || newPF < pathFactors.get(dstId)) {
                    pathFactors.put(dstId, newPF);
                    queue.add(dstId);
                }
            }
        }
        cache.put(origId, pathFactors); //Ergebnis cachen
        return pathFactors;

    }


    /**
     * Gibt an, ob PF(N1,N2)<=threshold
     * @param origId id des Ursprungsknotens
     * @param targetId id des Zielknotens
     * @param threshold PF-Schwellenwert
     * @return true, wenn PF<= threshold, sonst false
     */
    public boolean isInPfThreshold(String origId, String targetId, int threshold){
        Map<String,Integer> factors = computePfFrom(origId);
        return factors.getOrDefault(targetId, Integer.MAX_VALUE) <= threshold;
    }

}