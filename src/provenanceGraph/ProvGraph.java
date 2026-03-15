package provenanceGraph;

import Database.Graph.Edge;
import Database.Graph.Node;
import events.EventType;

import java.util.*;
import java.util.function.Predicate;

/**
 * Repräsentiert Graphen an sich nach außen
 */
public class ProvGraph {
    private final Map<String, Node> nodes = new HashMap<>();
    private final List<Edge> edges = new ArrayList<>();

    //Adjazenzliste hashID -> ausgehende Kanten
    private final Map<String,List<Edge>> outEdges = new HashMap<>();
    //Eingehende Kanten
    private final Map<String,List<Edge>> inEdges = new HashMap<>();

    /**
     * Fügt einen Knoten dem Graphen hinzu
     * @param node  Der Knoten, der hinzugefügt werden soll
     */
    public void addNode(Node node){
        String hashId = node.getHashId();
        nodes.put(hashId,node);
        outEdges.putIfAbsent(hashId,new ArrayList<>());
        inEdges.putIfAbsent(hashId,new ArrayList<>());
    }

    /**
     * Fügt eine Kante dem Graphen als Knoten hinzu
     * @param edge Kante die hinzugefügt werden soll
     */
    public void addEdge(Edge edge){
        edges.add(edge);
        String srcId = edge.getSrcNode().getHashId();
        String dstId = edge.getDstNode().getHashId();

        outEdges.computeIfAbsent(srcId, k -> new ArrayList<>()).add(edge);
        inEdges.computeIfAbsent(dstId, k-> new ArrayList<>()).add(edge);
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

    }

    /**
     * Traversierung mit Filterung anhand von Edge-Eigenschaften. Herausfinden, ob überhaupt nötig
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
     * Liefert alle Kanten, die der jeweiligen Operation entsprechen
     * @param operation Operations-Typ
     * @return Liste der passenden Kanten
     */
    public List<Edge> findEdgeByOperation(EventType.Type operation){
        List<Edge> out = new ArrayList<>();

        for (Edge e : edges){
            if(e.getOperation().equals(operation.toString())){
                out.add(e);
            }
        }
        return out;
    }

    /**
     * Vorwärts Traversierung.
     * @param hashId id des Knotens
     * @return Liste mit Edges, die von dem Knoten ausgehen
     */
    public List<Edge> getOutEdges(String hashId){
        if(outEdges.containsKey(hashId)){
            return outEdges.get(hashId);
        }
        System.out.println("[WARN] getOutEdges: ungültiger Knoten. Nutze Fallback");
        return Collections.emptyList();

    }

    /**
     * Liefert, welche Kanten, von einem Knoten ausgehen
     * @param hashId ID des Knotens
     * @return Liste mit allen ausgehenden Kanten
     */
    public List<Edge> getInEdges(String hashId){
        if (inEdges.containsKey(hashId)){
            return inEdges.get(hashId);
        }
        System.out.println("[WARN] getInEdges: ungültiger Knoten, nutze Fallback");
        return Collections.emptyList();
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
