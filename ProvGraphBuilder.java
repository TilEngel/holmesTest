import Database.JDBCEngine;

import java.util.List;
import java.util.Map;

public class ProvGraphBuilder {
    private static JDBCEngine engine;
    public ProvGraphBuilder(JDBCEngine engine){
        setEngine(engine);
    }

    public void collectData(){
        List<Map<String, Object>> subjects = engine.getAllNodes('1');
        List<Map<String,Object>> files = engine.getAllNodes('2');
        List<Map<String, Object>> netflow = engine.getAllNodes('3');

        List<Map<String,Object>> events = engine.getAllEvents();
    }

    public void setEngine(JDBCEngine engine){
        this.engine = engine;
    }
}
