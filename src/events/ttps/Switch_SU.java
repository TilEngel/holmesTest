package events.ttps;

import Database.Graph.Edge;
import Database.Graph.Subject;
import events.EventType;
import events.ttps.TTP;
import hsg.PathFactorEngine;
import provenanceGraph.ProvGraph;

import java.util.List;
import java.util.Set;

public class Switch_SU extends TTP {
    private static final Set<String> SUPERUSER_TOOLS = Set.of(
            "sudo", "su", "doas");

    public Switch_SU(PathFactorEngine engine){
        if (pfEngine ==null) {
            pfEngine = engine;
        }
        setSeverity('H');
        setType(EventType.Type.EVENT_CHANGE_PRINCIPAL);
        setPrerequisites(List.of(
                //Zielknoten muss Prozess sein
                (edge, graph) -> edge.getDstNode() instanceof Subject,
                //Prozessname ist Priv-Escalation-Tool
                ((edge, graph) -> {
                    if(!(edge.getDstNode() instanceof Subject)) return false;
                    Subject s =(Subject) edge.getDstNode();
                    return SUPERUSER_TOOLS.contains(s.getCmd());
                })
        ));
    }

    @Override
    public boolean matches(Edge edge, ProvGraph graph){
        if(!edge.getOperation().equals(EventType.Type.EVENT_CHANGE_PRINCIPAL.toString())){
            return false;
        }
        if(!prerequisitesMet(edge, graph)){
            return false;
        }

        return hasInitialCompromiseAncestor(edge.getDstNode(), graph);
    }

    @Override
    public String getName(){
        return "switch_su";
    }
}
