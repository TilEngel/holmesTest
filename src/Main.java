import Database.JDBCEngine;
import events.ttps.*;
import hsg.HSGBuilder;
import hsg.MatchingEngine;
import hsg.PathFactorEngine;
import provenanceGraph.ProvGraph;
import provenanceGraph.ProvGraphBuilder;

import java.sql.SQLException;
import java.util.List;

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
        ProvGraph graph = builder.getGraph();

        MatchingEngine MEngine = new MatchingEngine(graph);
        PathFactorEngine pf = new PathFactorEngine(graph);

        //Phasen an zu untersuchenden TTPs
        List<TTP> initialCompromise1 = List.of(new Untrusted_Read());
        //Auch Initial_Compromise, aber setzen Untrusted_Read voraus
        List<TTP> initialCompromise2 = List.of(new Make_Mem_Exec(pf), new Untrusted_File_Exec(pf));
        List<TTP> establishFoothold = List.of(new Shell_Exec(pf), new CnC(pf));
        List<TTP> privilegeEscalation = List.of(new Switch_SU(pf));
        List<TTP> internalRecon = List.of(new Sensitive_Command(pf));
        List<TTP> cleanupTracks = List.of(new Sensitive_Temp_RM(pf), new Clear_Logs(pf));

        MEngine.matchTTPs(List.of(
                initialCompromise1, initialCompromise2, establishFoothold,
                privilegeEscalation, internalRecon, cleanupTracks
        ));

        HSGBuilder hsgBuilder = new HSGBuilder(graph, pf);
        hsgBuilder.constructHSG();
    }
}
