package events.ttps;
import Database.Graph.Edge;
import Database.Graph.Netflow;
import events.EventType;
import events.ttps.TTP;
import provenanceGraph.ProvGraph;

import java.util.List;
import java.util.Set;

/**
 * Untrusted_Read in Initial_Compromise
 * unbekannte IP liest
 * Ausgangspunkt für alle weiteren TTPs
 * Es kann nicht zuverlässig gesagt werden, welche IPs vertrauenswürdig sind
 * und welche nicht :( Stattdessen alle Netflows-reads als untrusted melden
 */
public class Untrusted_Read extends TTP {

    private static final Set<String> TRUSTED_IPS = Set.of(
            "128.55.12.10" //z.B
    );

    public Untrusted_Read(){
        setSeverity('L');
        setType(EventType.Type.EVENT_RECVFROM);
        setPrerequisites(List.of(
                //Quellknoten muss untrusted IP haben
                (edge, graph) -> {
                    if(!(edge.getSrcNode() instanceof Netflow)) return false;
                    Netflow n = (Netflow) edge.getSrcNode();
                    return !TRUSTED_IPS.contains(n.getDstAddr());
                }
        ));
    }

    @Override
    public boolean matchesOld(Edge edge, ProvGraph graph){
        if(!edge.getOperation().equals(EventType.Type.EVENT_RECVFROM.toString())){
            return false;
        }
        return prerequisitesMet(edge, graph);
    }

    @Override
    public boolean matches(Edge edge, ProvGraph graph){
        if(!edge.getOperation().equals(EventType.Type.EVENT_RECVFROM.toString())){
            return false;
        }
        return prerequisitesMet(edge, graph);
    }
    @Override
    public String getName(){
        return "untrusted_read";
    }

}
