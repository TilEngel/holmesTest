package hsg;

import Database.Graph.Edge;
import Database.Graph.Node;
import events.TTP;
import events.Untrusted_Read;
import provenanceGraph.ProvGraph;

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
        Untrusted_Read untrustedRead = new Untrusted_Read();
        Map<String, Map<String, Integer>> pfCache = new HashMap<>();
        // Zuerst Initial Compromise, sonst race Conditions
        for (Edge e: graph.getEdges()){
            if(untrustedRead.matches(e, graph)){
                Node match = e.getDstNode();
                match.addTTP(untrustedRead);
                System.out.println("[TTP MATCH] "+ untrustedRead.getName() + " auf Knoten "+ match.getName());
                String hashId = match.getHashId();
                //PF berechnen (wenn nötig)
                if(!pfCache.containsKey(hashId)){
                    pfCache.put(hashId,engine.computePfFrom(hashId));
                }
            }
        }
        //Spätere TTPs
        for(Edge e : graph.getEdges()){
            for(TTP ttp: ttps){
                if(ttp.matches(e, graph)){
                    Node match = e.getDstNode();
                    match.addTTP(ttp);
                    System.out.println("[TTP MATCH] " + ttp.getName() + " auf Knoten "+ match.getName());
                    String hashId = match.getHashId();
                    if(!pfCache.containsKey(hashId)){
                        pfCache.put(hashId, engine.computePfFrom(hashId));

                    }
                }
            }
        }
    }

}
