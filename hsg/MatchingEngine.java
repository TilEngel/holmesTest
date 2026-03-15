package hsg;

import Database.Graph.Edge;
import Database.Graph.Netflow;
import Database.Graph.Node;
import Events.TTP;
import ProvenanceGraph.ProvGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Durchläuft jede Kante im Graphen. Sucht nach TTP-Matches und prüft PF
     * @param ttps Liste an TTPs, nach denen gesucht werden soll
     */
    public void matchTTPs(List<TTP> ttps){
        PathFactorEngine engine = new PathFactorEngine(graph);
        Map<String, Map<String, Integer>> pfCache = new HashMap<>();
        for(Edge e : graph.getEdges()){
            //TTP-Matching
            for(TTP ttp: ttps){
                //Falls Match
                if(ttp.matches(e,graph)){
                    Node matched = e.getDstNode();
                    matched.addTTP(ttp);
                    System.out.println("[TTP MATCH] " + ttp.getName()+ " auf Knoten "+ matched.getName());
                    //pfEngine für Knoten anlegen, falls noch nicht existiert
                    String matchedHashId= matched.getHashId();
                    if(!pfCache.containsKey(matchedHashId)){
                        pfCache.put(matchedHashId, engine.computePfFrom(matchedHashId));
                        //Anschließend sowas wie hsgBuilder.addNode()
                    }
                }
            }
        }
        for (String originId : pfCache.keySet()) {
            System.out.println("[PathFactor] Ursprung: "
                    + graph.getNode(originId).getName());
            for (Map.Entry<String, Integer> entry : pfCache.get(originId).entrySet()) {
                System.out.println("  -> " + graph.getNode(entry.getKey()).getName()
                        + " : " + entry.getValue());
            }
        }

    }

}
