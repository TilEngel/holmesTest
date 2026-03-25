
package events.ttps;

import Database.Graph.Edge;
import Database.Graph.File;
import Database.Graph.Subject;
import events.EventType;
import hsg.PathFactorEngine;
import provenanceGraph.ProvGraph;

import java.util.List;

/**
 * Untrusted_File_Exec in Initial_Compromise
 * nach Untrusted Read wird eine Datei ausgeführt
 */
public class Untrusted_File_Exec extends TTP{
    public Untrusted_File_Exec(PathFactorEngine engine){
        if (pfEngine ==null) {
            pfEngine = engine;
        }
        setSeverity('C');
        setType(EventType.Type.EVENT_EXECUTE);
        setPrerequisites(List.of(
                //Quellknoten ist Datei
                (edge, graph) -> edge.getSrcNode() instanceof File,
                //Zielknoten ist Prozess
                (edge, graph) -> edge.getDstNode() instanceof Subject
        ));

    }
    @Override
    public boolean matchesOld(Edge edge, ProvGraph graph){
        if(!edge.getOperation().equals(EventType.Type.EVENT_EXECUTE.toString())){
            return false;
        }
        if(!prerequisitesMet(edge,graph)){
            return false;
        }
        return hasUntrustedReadAncestor(edge.getSrcNode(), graph);
    }

    @Override
    public boolean matches(Edge edge, ProvGraph graph){
        if(!edge.getOperation().equals(EventType.Type.EVENT_EXECUTE.toString())){
            return false;
        }
        if(!prerequisitesMet(edge,graph)){
            return false;
        }
        return true;
    }

    @Override
    public String getName(){
        return "untrusted_file_exec";
    }
}