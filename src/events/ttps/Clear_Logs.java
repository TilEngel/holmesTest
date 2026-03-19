package events.ttps;

import Database.Graph.Edge;
import Database.Graph.File;
import Database.Graph.Subject;
import events.EventType;
import hsg.PathFactorEngine;
import provenanceGraph.ProvGraph;

import java.util.List;
import java.util.Set;

/**
 * Clear_Logs in Cleanup_Tracks
 * Nach Initial_Compromise werden Dateien in Log-Ordnern gelöscht
 */
public class Clear_Logs extends TTP{

    private static final Set<String> LOG_PATHS = Set.of(
            "/var/log/",
            "/usr/log/",
            "/var/adm/",
            "/var/run/"
    );

    public Clear_Logs(PathFactorEngine engine){
        if (pfEngine ==null) {
            pfEngine = engine;
        }
        setSeverity('H');
        setType(EventType.Type.EVENT_UNLINK);
        setPrerequisites(List.of(
                //SrcKnoten muss Prozess sein
                (edge, graph) -> edge.getSrcNode() instanceof Subject,
                //Zielknoten muss Datei sein
                (edge, graph) -> edge.getDstNode() instanceof File,
                (edge,graph)->{
                    if(!(edge.getDstNode() instanceof  File)) return false;
                    File f= (File) edge.getDstNode();
                    for(String path: LOG_PATHS){
                        if(f.getPath().startsWith(path)){
                            return true;
                        }
                    }
                    return false;
                }
        ));
    }

    @Override
    public boolean matches(Edge edge, ProvGraph graph){
        if(!edge.getOperation().equals(EventType.Type.EVENT_UNLINK.toString())){
            return false;
        }

        if(!prerequisitesMet(edge,graph)){
            return false;
        }
        return hasInitialCompromiseAncestor(edge.getSrcNode(), graph);
    }

    @Override
    public String getName(){
        return "clear_logs";
    }
}
