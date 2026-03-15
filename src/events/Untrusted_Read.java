package events;
import Database.Graph.Edge;
import Database.Graph.Netflow;
import provenanceGraph.ProvGraph;

import java.util.List;
import java.util.Set;

public class Untrusted_Read extends TTP{

    private static final Set<String> TRUSTED_IPS = Set.of(
            "10.0.67.23" //z.B
    );

    public Untrusted_Read(){
        setSeverity('L');
        setType(EventType.Type.EVENT_RECVFROM);
        setPrerequisites(List.of(
                //Quellknoten muss untrusted IP haben
                (node, graph) -> {
                    if(!(node instanceof Netflow)) return false;
                    Netflow n = (Netflow) node;
                    return !TRUSTED_IPS.contains(n.getDstAddr());
                }
        ));
    }

    @Override
    public boolean matches(Edge edge, ProvGraph graph){
        if(!edge.getOperation().equals(EventType.Type.EVENT_RECVFROM.toString())){
            return false;
        }
        return prerequisitesMet(edge.getSrcNode(), graph);
    }
    @Override
    public String getName(){
        return "untrusted_read";
    }

}
