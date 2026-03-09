import Database.Graph.*;
import Database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProvGraphBuilder {
    //Engine, um Daten aus der Datenbank zu holen
    private static JDBCEngine engine;
    //Mapping Hash_ID->Node-Objekt ermöglicht Zugriff in O(1)
    private final Map<String,Node> nodeIndex = new HashMap<>();
    //Listen, in denen Knoten und Kanten gespeichert werden
    private final List<Node> nodes = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();


    public ProvGraphBuilder(JDBCEngine engine){
        setEngine(engine);
    }

    public void collectData(){
        //Knoten
        collectSubjects();
        collectFiles();
        collectNetflows();
        //Kanten
        collectEvents();
    }

    /**
     * Erstellt Subjekt-Instanzen und legt sie in nodeIndex-Liste ab
     */
    private void collectSubjects(){
        List<Map<String, Object>> rows = engine.getAllNodes('1');
        for(Map<String,Object> row : rows) {
            String uuid = (String) row.get("node_uuid");
            long nodeIndex = toLong(row.get("index_id"));
            String path = (String) row.get("path");
            String cmd = (String) row.get("cmd");
            String hashId = (String) row.get("hash_id");

            Subject s = new Subject(uuid, nodeIndex,path,cmd);
            nodes.add(s);
            this.nodeIndex.put(hashId,s);
        }
        System.out.println("[INFO] Subjects geladen: " + nodes.size());

    }

    /**
     * Erstellt File-Instanzen und legt sie in nodeIndex-Liste ab
     */
    private void collectFiles(){
        List<Map<String, Object>> rows = engine.getAllNodes('2');
        for(Map<String,Object> row : rows) {
            String uuid = (String) row.get("node_uuid");
            long nodeIndex = toLong(row.get("index_id"));
            String path = (String) row.get("path");
            String hashId = (String) row.get("hash_id");

            File f = new File(uuid, nodeIndex,path);
            nodes.add(f);
            this.nodeIndex.put(hashId,f);
        }
        System.out.println("[INFO] Files geladen: "+ nodes.size());

    }

    /**
     * Erstellt Netflow-Instanzen und legt sie in nodeIndex-Liste ab
     */
    private void collectNetflows(){
        List<Map<String, Object>> rows = engine.getAllNodes('3');
        for(Map<String,Object> row : rows) {

            String uuid = (String) row.get("node_uuid");
            long nodeIndex = toLong(row.get("index_id"));
            String srcAddr = (String) row.get("src_addr");
            String srcPort = (String) row.get("src_port");
            String dstAddr = (String) row.get("dst_addr");
            String dstPort = (String)row.get("dst_port");
            String hashId = (String) row.get("hash_id");

            Netflow n = new Netflow(uuid, nodeIndex, srcAddr,srcPort,dstAddr,dstPort);
            nodes.add(n);
            this.nodeIndex.put(hashId,n);
        }
        System.out.println("[INFO] Netflows geladen: "+nodes.size());

    }

    /**
     * Erstellt Edge-Instanzen mit Verweisen auf beteiligte Knoten
     * legt diese in edges-Liste ab
     */
    private void collectEvents(){
        List<Map<String,Object>> rows = engine.getAllEvents();
        int skipped =0;

        for (Map<String,Object> row:rows ){
            String srcId = (String) row.get("src_node");
            String dstId = (String) row.get("dst_node");
            //jwlg. Knoten-Instanzen aus NodeIndex holen
            Node srcNode = nodeIndex.get(srcId);
            Node dstNode = nodeIndex.get(dstId);

            // falls einer der Knoten nicht im Zeitfenster liegt
            if(srcNode == null || dstNode == null){
                skipped++;
                continue;
            }
            String eventUuid = (String) row.get("event_uuid");
            String operation = (String) row.get("operation");
            String timestamp = String.valueOf(row.get("timestamp_rec"));
            long id = toLong(row.get("_id"));

            Edge e = new Edge(srcNode,operation,dstNode,eventUuid,timestamp,id);
            edges.add(e);
        }
        System.out.println("[INFO] Edges geladen: "+edges.size()+ " | skipped: "+ skipped);
    }

    public void printEdges() {
        for(Edge e : edges) {
            String srcNode = "| "+e.getSrcNode().getName()+ " |";
            String dstNode = "| " + e.getDstNode().getName()+ " |";
            System.out.println(srcNode + " ---" + e.getOperation() + "---> " + dstNode);
        }
    }


    public void setEngine(JDBCEngine engine){
        this.engine = engine;
    }

    /*
    Hilfsmethode, um Object zu long zu konvertieren
     */
    private long toLong(Object obj){
        if(obj instanceof Long) return (Long) obj;
        if(obj instanceof Integer) return ((Integer)obj).longValue();
        if(obj instanceof String) return Long.parseLong((String)obj);
        throw new IllegalArgumentException("[ERR] toLong nicht möglich: "+obj);
    }
}
