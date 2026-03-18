package events;

import Database.Graph.Edge;
import Database.Graph.Node;
import java.util.List;
import java.util.Map;

import hsg.PathFactorEngine;
import provenanceGraph.ProvGraph;


public abstract class TTP {
    static final int PF_THRESHOLD = 2;

    private char severity;

    private EventType.Type type;

    private List<Prerequisite> prerequisites;
    PathFactorEngine pfEngine;

    public abstract boolean matches(Edge edge, ProvGraph graph);

    //gibt TTP-Name zurück, welcher in Node.ttps gespeichert wird
    public abstract String getName();

    /**
     * Prüft für alle Prärekonditionen des entsprechenden
     * TTPs, ob sie eingehalten werden
     * @param edge Die Kante dessen Knoten geprüft wird
     * @param graph Provenance-Graph
     * @return true, wenn alle Bedingungen eingehalten werden, sonst false
     */
    protected boolean prerequisitesMet(Edge edge, ProvGraph graph ){
        for(Prerequisite p : prerequisites){
            if(!p.evaluate(edge, graph)){
                return false;
            }
        }
        return true;
    }

    /**
     * Prüft, ob der Knoten nach einem potenziellen
     * Initial Compromise folgt, unter Prüfung des PFs
     * @param target Zielknoten
     * @param graph Provenance-Graph
     * @return true, wenn Bedingung erfüllt, false sonst
     */
    boolean hasInitialCompromiseAncestor(Node target, ProvGraph graph){
        Untrusted_Read uR = new Untrusted_Read();
        Make_Mem_Exec mME = new Make_Mem_Exec(pfEngine);

        for(Node candidate: graph.getNodes().values()){
            if(candidate.hasMatchedTTP(uR)|| candidate.hasMatchedTTP(mME)){
                if(pfEngine.isInPfThreshold(candidate.getHashId(),target.getHashId(), PF_THRESHOLD)){
                    return true;
                }
            }
        }
        return false;
    }

    EventType.Type getType(){
        return type;
    }
    char getSeverity(){
        return severity;
    }

    protected void setType(EventType.Type type){
        this.type= type;
    }
    protected void setSeverity(char severity){
        this.severity = severity;
    }

    protected void setPrerequisites(List<Prerequisite> prerequisites){
        this.prerequisites = prerequisites;
    }
}
