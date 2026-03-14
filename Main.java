import Database.JDBCEngine;
import Events.Untrusted_Read;
import ProvenanceGraph.MatchingEngine;
import ProvenanceGraph.ProvGraphBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
            System.out.println("[ERR] Fehler in Main: "+ e.getMessage());
        }
    }
}
