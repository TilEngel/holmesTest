import Database.Graph.Edge;
import Database.Graph.Node;

import java.util.*;
import java.util.function.Predicate;

public class ProvGraph {
    private final Map<String, Node> nodes = new HashMap<>();
    private final List<Edge> edges = new ArrayList<>();

    //Adjazenzliste hashID -> ausgehende Kanten
    private final Map<String,List<Edge>> outEdges = new HashMap<>();

    /**
     * Fügt einen Knoten dem Graphen hinzu
     * @param node  Der Knoten, der hinzugefügt werden soll
     */
    public void addNode(Node node){
        String hashId = node.getHashId();
        nodes.put(hashId,node);
        outEdges.putIfAbsent(hashId,new ArrayList<>());
    }

    /**
     * Fügt eine Kante dem Graphen als Knoten hinzu
     * @param edge Kante die hinzugefügt werden soll
     */
    public void addEdge(Edge edge){
        edges.add(edge);
        String srcId = edge.getSrcNode().getHashId();

        outEdges.computeIfAbsent(srcId, k -> new ArrayList<>()).add(edge);
    }

    /**
     * Liefert Liste mit allen Knoten, die vom Startpunkt aus erreicht werden können
     * @param startHashId ID des Start-Knotens
     * @return Liste aller erreichbarer Knoten (inklusive Startknoten)
     */
    public Set<Node> traverseForwardFrom(String startHashId){
        Set<Node> visited = new LinkedHashSet<>();
        Queue<String> queue = new LinkedList<>();
        int count=0;

        queue.add(startHashId);
        while(!queue.isEmpty()){
            String current = queue.poll();

            if(visited.contains(nodes.get(current))){
                continue;
            }
            visited.add(nodes.get(current));
            //alle verbundenen Knoten besuchen
            for(Edge e : getOutEdges(current)){
                String dstId = e.getDstNode().getHashId();
                //Falls erster Besuch hinzufügen
                if(!visited.contains(nodes.get(dstId))){
                    queue.add(dstId);
                    count++;
                }
            }
        }
        System.out.println("Von Knoten " + getNode(startHashId).getName() + " können " + count +" Knoten erreicht werden");
        return visited;
        /*
         * Schauen in Paper, wie traversiert werden muss (auch rückwärts?)
         * Test Methode, die traversierung nutzt
         */
    }

    /**
     * Traversierung mit Filterung anhand von Edge-Eigenschaften
     * @param startHashId ID des Knotens an dem gestartet wird
     * @param edgeFilter Liste an Filtern
     * @return Liste aller Knoten, die mit Bedingungen erreicht werden können
     */
    public Set<Node> traverseForwardFilter(String startHashId, Predicate<Edge> edgeFilter){
        Set<Node> visited = new LinkedHashSet<>();
        Queue<String> queue = new LinkedList<>();

        queue.add(startHashId);

        while(!queue.isEmpty()){
            String curId = queue.poll();
            Node curNode = nodes.get(curId);
            if(visited.contains(curNode)){
                continue;
            }
            visited.add(curNode);

            for (Edge e: getOutEdges(curId)){
                if(!edgeFilter.test(e)){
                    continue;
                }
                String dstId = e.getDstNode().getHashId();
                if(!visited.contains(nodes.get(dstId))){
                    queue.add(dstId);
                }
            }
        }
        return visited;
    }

    /**
     * Vorwärts Traversierung.
     * @param hashId id des Knotens
     * @return Liste mit Edges, die von dem Knoten ausgehen
     */
    public List<Edge> getOutEdges(String hashId){
        if(outEdges.containsKey(hashId)){
            return outEdges.get(hashId);
        } else{
            System.out.println("[WARN] getOutEdges: ungültiger Knoten. Nutze Fallback");
            return Collections.emptyList();
        }
    }

    /**
     * Liefert Knoten mit entsprechender id
     * @param hashId ID des gesuchten Knotens
     * @return Knoten mit der ID
     */
    public Node getNode(String hashId){
        return nodes.get(hashId);
    }
    public Map<String,Node> getNodes(){
        return nodes;
    }
    public List<Edge> getEdges() {
        return edges;
    }
}
