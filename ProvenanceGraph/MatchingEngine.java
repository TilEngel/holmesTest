package ProvenanceGraph;

import Database.Graph.Edge;
import Database.Graph.Netflow;
import Events.TTP;

import java.util.List;

public class MatchingEngine {
    ProvGraph graph;
    public MatchingEngine(ProvGraph graph){
        this.graph = graph;
    }

    public void matchTTPs(List<TTP> ttps){

        for(Edge eg : graph.getEdges()){
            for (TTP ttp: ttps){
                if(ttp.matches(eg,graph)){
                    eg.getDstNode().addTTP(ttp);
                    System.out.println("[TTP MATCH] auf Knoten "+ eg.getDstNode().getName());
                }
            }
        }
    }
}
