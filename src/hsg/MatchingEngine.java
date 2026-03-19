package hsg;

import Database.Graph.Edge;
import Database.Graph.Node;
import events.ttps.TTP;
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
     * @param ttps Phasenweise Listen an TTPs, nach denen gesucht werden soll
     */
    public void matchTTPs(List<List<TTP>> ttps){
        PathFactorEngine engine = new PathFactorEngine(graph);
        //TTPs Phasenweise durchgehen (sonst Gefahr von Race-Conditions)
        for(List<TTP> list : ttps) {
            for(Edge e : graph.getEdges()){
                for (TTP ttp : list) {
                    //falls Bedingungen erfüllt sind
                    if (ttp.matches(e, graph)) {
                        Node match = e.getDstNode();
                        match.addTTP(ttp);
                        System.out.println("[TTP MATCH] " + ttp.getName() + " auf Knoten " + match.getName());
                    }
                }
            }
        }
    }

}
