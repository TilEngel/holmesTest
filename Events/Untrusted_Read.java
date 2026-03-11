package Events;
import Database.Graph.Edge;
import Database.Graph.Netflow;
import ProvenanceGraph.ProvGraph;

import java.util.List;
import java.util.Set;

public class Untrusted_Read extends TTP{

    private static final Set<String> TRUSTED_IPS = Set.of(
            "192.168.1.0/24" //z.B
    );

    public Untrusted_Read(){
        setSeverity('L');
        setType(EventType.Type.EVENT_READ);
        setPrerequisites(List.of(
                //Quellknoten muss untrusted IP haben
                (node, graph) -> {
                    if(!(node instanceof Netflow)) return false;
                    Netflow n = (Netflow) node;
                    return !TRUSTED_IPS.contains(n.getSrcAddr());
                }
        ));
    }

    @Override
    public boolean matches(Edge edge, ProvGraph graph){
        if(!edge.getOperation().equals(EventType.Type.EVENT_READ.toString())){
            return false;
        }
        return prerequisitesMet(edge.getSrcNode(), graph);
    }
}
