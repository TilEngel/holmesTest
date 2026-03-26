package events.ttps;

import Database.Graph.Subject;
import Database.Graph.File;
import Database.Graph.Edge;
import events.EventType;
import events.ttps.TTP;
import hsg.PathFactorEngine;
import provenanceGraph.ProvGraph;

import java.util.List;

/**
 * Sensitive_Temp_RM in Cleanup_Tracks
 * Nach Initial_Compromise und Internal_Recon wird Datei gelöscht
 */
public class Sensitive_Temp_RM extends TTP {
    public Sensitive_Temp_RM(PathFactorEngine engine){
        if (pfEngine ==null) {
            pfEngine = engine;
        }
        setSeverity('M');
        setType(EventType.Type.EVENT_UNLINK);
        setPrerequisites(List.of(
                //SrcNode muss Prozess sein
                (edge, graph) -> edge.getSrcNode() instanceof Subject,
                //Zielknoten muss Datei sein
                (edge,graph) -> edge.getDstNode() instanceof File
        ));
    }

    @Override
    public  boolean matchesOld(Edge edge, ProvGraph graph){
        if(!edge.getOperation().equals(EventType.Type.EVENT_UNLINK.toString())){
            return false;
        }
        if(!prerequisitesMet(edge, graph)){
            return false;
        }
        return hasInitialCompromiseAncestor(edge.getSrcNode(), graph)
                && hasInternalReconAncestor(edge.getDstNode(), graph);
    }

    @Override
    public  boolean matches(Edge edge, ProvGraph graph){
        if(!edge.getOperation().equals(EventType.Type.EVENT_UNLINK.toString())){
            return false;
        }
        if(!prerequisitesMet(edge, graph)){
            return false;
        }
        return true;
    }

    @Override
    public String getName(){
        return "sensitive_temp_rm";
    }
}
