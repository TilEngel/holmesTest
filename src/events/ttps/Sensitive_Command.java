package events.ttps;

import Database.Graph.Subject;
import events.EventType;
import events.ttps.TTP;
import hsg.PathFactorEngine;
import Database.Graph.Edge;
import provenanceGraph.ProvGraph;

import java.util.Set;
import java.util.List;

/**
 * Sensitive_Command in Internal_Recon
 * Nach Initial_Compromise wird ein potentiell gefährlicher Command verwendet
 */
public class Sensitive_Command extends TTP {
    private static final Set<String> SENSITIVE_COMMANDS = Set.of(
            "whoami", "hostname", "ifconfig", "netstat", "uname",
            //nicht explizit genannt aber auch sinnvoll
            "id", "ps", "w", "who", "last", "find", "locate"
    );

    public Sensitive_Command(PathFactorEngine engine){
        if (pfEngine ==null) {
            pfEngine = engine;
        }
        setSeverity('H');
        setType(EventType.Type.EVENT_FORK);
        setPrerequisites(List.of(
                //Quellknoten muss Prozess sein
                (edge,graph) -> edge.getSrcNode() instanceof Subject,
                //Zielknoten muss Prozess sein
                (edge, graph) -> edge.getDstNode() instanceof Subject,
                //Zielknoten muss sensitiver Befehl sein
                (edge, graph) -> {
                    if(!(edge.getDstNode() instanceof Subject)) return  false;
                    Subject s = (Subject) edge.getDstNode();
                    return SENSITIVE_COMMANDS.contains(s.getCmd());
                }
        ));
    }

    @Override
    public boolean matchesOld(Edge edge, ProvGraph graph){
        if(!edge.getOperation().equals(EventType.Type.EVENT_FORK.toString())){
            return false;
        }
        if(!prerequisitesMet(edge,graph)){
            return false;
        }
        return hasInitialCompromiseAncestor(edge.getSrcNode(), graph);
    }

    @Override
    public boolean matches(Edge edge, ProvGraph graph){
        if(!edge.getOperation().equals(EventType.Type.EVENT_FORK.toString())){
            return false;
        }
        if(!prerequisitesMet(edge,graph)){
            return false;
        }
        return true;
    }

    @Override
    public String getName(){
        return "sensitive_command";
    }
    @Override
    public String getPhase(){
        return "internal_recon";
    }
}
