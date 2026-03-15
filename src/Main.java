import Database.JDBCEngine;
import provenanceGraph.ProvGraphBuilder;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args){
        JDBCEngine engine = new JDBCEngine();
        try{
            engine.connect();
            ProvGraphBuilder builder = new ProvGraphBuilder(engine);
            builder.collectData();
            builder.printEdges();
            engine.disconnect();
        } catch (SQLException e){
            System.out.println("[ERR] Fehler in src.Main: "+ e.getMessage());
        }
    }
}
