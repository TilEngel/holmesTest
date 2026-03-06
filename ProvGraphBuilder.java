import Database.Graph.*;
import Database.*;

import java.util.List;
import java.util.Map;

public class ProvGraphBuilder {
    private static JDBCEngine engine;
    public ProvGraphBuilder(JDBCEngine engine){
        setEngine(engine);
    }

    public void collectData(){
        collectSubjects();
        collectFiles();
        collectNetflows();
        collectEvents();
    }
    private void collectSubjects(){
        List<Map<String, Object>> subjects = engine.getAllNodes('1');

        for(Map<String,Object> subject : subjects) {
            //Node s = new Subject();
        }

    }
    private void collectFiles(){
        List<Map<String,Object>> files = engine.getAllNodes('2');
    }
    private void collectNetflows(){
        List<Map<String, Object>> netflow = engine.getAllNodes('3');
    }
    private void collectEvents(){
        List<Map<String,Object>> events = engine.getAllEvents();
    }


    public void setEngine(JDBCEngine engine){
        this.engine = engine;
    }
}
