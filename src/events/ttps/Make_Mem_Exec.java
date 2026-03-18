package events.ttps;

import Database.Graph.Edge;
import Database.Graph.Subject;
import events.EventType;
import hsg.PathFactorEngine;
import provenanceGraph.ProvGraph;
import Database.Graph.Node;
import java.util.List;



public class Make_Mem_Exec extends TTP {

    public Make_Mem_Exec(PathFactorEngine engine){
        this.pfEngine = engine;
        setSeverity('M');
        setType(EventType.Type.EVENT_MODIFY_PROCESS);
        //Zielknoten muss Prozess sein
        setPrerequisites(List.of((edge, graph) -> edge.getDstNode() instanceof Subject));
    }

    @Override
    public boolean matches(Edge edge, ProvGraph  graph){

        //Event muss MODIFY_PROCESS sein
        if(!edge.getOperation().equals(EventType.Type.EVENT_MODIFY_PROCESS.toString())){
            return false;
        }
        //Prüfen, ob Zielknoten Prozess
        if(!prerequisitesMet(edge, graph)){
            return false;
        }
        //kommt vorher Untrusted_read?
        return hasUntrustedReadAncestor(edge.getDstNode(),graph);

    }

    /**
     * Prüft, ob ein Untrusted Read mit
     * PathFactor<= PF_THRESHOLD existiert
     * @param target Zielknoten
     * @param graph Provenance-Graph
     * @return true, wenn Bedingungen erfüllt, false, wenn nict
     */
    private  boolean hasUntrustedReadAncestor(Node target, ProvGraph graph){
        Untrusted_Read uR = new Untrusted_Read();

        for(Node candidate : graph.getNodes().values()){
            if(candidate.hasMatchedTTP(uR)){
                if(pfEngine.isInPfThreshold(candidate.getHashId(), target.getHashId(), PF_THRESHOLD)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getName(){
        return "make_mem_exec";
    }
}
