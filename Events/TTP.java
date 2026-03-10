package Events;

import java.util.List;

public class TTP {

    private char severity;

    private EventType type;

    private List<String> prerequisites;

    EventType getType(){
        return type;
    }
    char getSeverity(){
        return severity;
    }

    void setType(EventType type){
        this.type= type;
    }
    void setSeverity(char severity){
        this.severity = severity;
    }
}
