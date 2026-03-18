package events;

import Database.Graph.Edge;
import Database.Graph.Subject;
import hsg.PathFactorEngine;
import Database.Graph.File;
import Database.Graph.Node;
import provenanceGraph.ProvGraph;

import java.util.List;
import java.util.Set;

public class Shell_Exec extends TTP {
    private static final Set<String> SHELL_PATHS = Set.of(
            "/bin/bash",
            "/bin/sh",
            "/bin/dash",
            "/bin/zsh",
            "cmd.exe",
            "powershell.exe"
    );

    public Shell_Exec(PathFactorEngine engine){
        this.pfEngine = engine;
        setSeverity('M');
        setType(EventType.Type.EVENT_EXECUTE);
        setPrerequisites(List.of(
                //Zielknoten muss Prozess sein
                (edge, graph) -> edge.getDstNode() instanceof Subject,
                (edge, graph) -> {
                    //Quellknoten muss bekannte Shell sein
                    if(!(edge.getSrcNode() instanceof File)) return false;
                    File f = (File) edge.getSrcNode();
                    return SHELL_PATHS.contains(f.getPath());
                }
        ));
    }

    @Override
    public boolean matches(Edge edge, ProvGraph graph){
        if(!edge.getOperation().equals(EventType.Type.EVENT_EXECUTE.toString())){
            return false;
        }
        if(!prerequisitesMet(edge, graph)){
            return false;
        }
        return hasInitialCompromiseAncestor(edge.getDstNode(), graph);
    }



    @Override
    public String getName(){
        return "shell_exec";
    }
}
