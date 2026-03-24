import Database.JDBCEngine;
import provenanceGraph.ProvGraphBuilder;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args){
        JDBCEngine engine = new JDBCEngine();
        ProvGraphBuilder builder = new ProvGraphBuilder(engine);
        try{
            engine.connect();

            builder.collectData();

            engine.disconnect();
        } catch (SQLException e){
            System.out.println("[ERR] Fehler in src.Main: "+ e.getMessage());
        }
        builder.printEdges();
    }
}
