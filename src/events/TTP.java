package events;

import Database.Graph.Edge;
import Database.Graph.Node;
import java.util.List;
import java.util.Map;

import provenanceGraph.ProvGraph;


public abstract class TTP {
    static final int PF_THRESHOLD = 3;

    private char severity;

    private EventType.Type type;

    private List<Prerequisite> prerequisites;

    public abstract boolean matches(Edge edge, ProvGraph graph);

    //vllt. unnötig, gibt Klassennahmen zurück
    public abstract String getName();

    protected boolean prerequisitesMet(Edge edge, ProvGraph graph ){
        for(Prerequisite p : prerequisites){
            if(!p.evaluate(edge, graph)){
                return false;
            }
        }
        return true;
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
