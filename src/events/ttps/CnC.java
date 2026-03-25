package events.ttps;

import Database.Graph.Edge;
import Database.Graph.Netflow;
import Database.Graph.Subject;
import events.EventType;
import hsg.PathFactorEngine;
import provenanceGraph.ProvGraph;

import java.util.List;
/**
 * CnC in Establish_Foothold
 * Unbekannte IP sendet zu Netflow
 * ip != trustedIP wird ersetzt durch hasInitialCompromiseAncestor()
  */

public class CnC extends TTP{

    public CnC(PathFactorEngine engine){
        if(pfEngine==null){
            pfEngine= engine;
        }
        setSeverity('H');
        setType(EventType.Type.EVENT_SENDTO);
        setPrerequisites(List.of(
                //Quellknoten ist Prozess
                (edge, graph) -> edge.getSrcNode() instanceof Subject,
                //Zielknoten ist Netflow
                (edge, graph)-> edge.getDstNode() instanceof Netflow
        ));
    }
    @Override
    public boolean matchesOld(Edge edge, ProvGraph graph){
        if(!edge.getOperation().equals(EventType.Type.EVENT_SENDTO.toString())){
            return false;
        }
        if(!prerequisitesMet(edge,graph)){
            return false;
        }
        //hier srcNode prüfen, weil der Prozess ist
        return hasInitialCompromiseAncestor(edge.getSrcNode(),graph);
    }

    @Override
    public boolean matches(Edge edge, ProvGraph graph){
        if(!edge.getOperation().equals(EventType.Type.EVENT_SENDTO.toString())){
            return false;
        }
        if(!prerequisitesMet(edge,graph)){
            return false;
        }
        //hier srcNode prüfen, weil der Prozess ist
        return true;
    }

    @Override
    public String getName(){
        return "cnc";
    }
}
