import Database.JDBCEngine;

import java.sql.ResultSet;
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
            System.out.println("[ERR] Fehler in Main: "+ e.getMessage());
        }
    }
}
