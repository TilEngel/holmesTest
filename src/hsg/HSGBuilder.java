package hsg;

import Database.Graph.Node;
import events.ttps.TTP;
import provenanceGraph.ProvGraph;

import java.util.*;

/**
 * Stellt den HSG nach außen dar.
 * Kann erzeugt und ausgegeben werden, sonst noch nicht so viel
 */
public class HSGBuilder {
    private final ProvGraph graph;
    private final PathFactorEngine pfEngine;

    private final Map<String, Set<String>> hsgEdges = new HashMap<>();
    private final Map<String,Set<String>> incomingEdges = new HashMap<>();
    private final Map<String, Node> hsgNodes = new HashMap<>();

    public HSGBuilder(ProvGraph graph, PathFactorEngine pfEngine){
        this.graph = graph;
        this.pfEngine = pfEngine;
    }

    /**
     * Sammelt alle Knoten, an denen TTPs gefunden wurden.
     * Verbindet sie unter Berücksichtigung des PathFactors.
     * Gibt Verbindungen auf Konsole aus.
     */
    public void constructHSG(){
        Map<String, List<Node>> scenarios = new HashMap<>();

        for(Node node: graph.getNodes().values()){
            for(TTPChain chain: node.getChains()){
                String origin = chain.getOriginId();
                if(!scenarios.containsKey(origin)){
                    scenarios.put(origin, new ArrayList<>());
                }
                if(!scenarios.get(origin).contains(node)){
                    scenarios.get(origin).add(node);
                }
            }
        }
        //Ausgabe
        int count= 0;
        for(Map.Entry<String,List<Node>> entry: scenarios.entrySet()){
            count++;
            Node origin = graph.getNode(entry.getKey());
            List<Node> involved =  entry.getValue();

            System.out.println("\n Szenario "+ count);
            System.out.println("Ursprung: "+ origin.getName());
            System.out.println("Beteiligte Knoten: "+ involved.size());

            Set<String> allTTPs = new LinkedHashSet<>();
            for(Node n : involved){
                for(TTPChain chain: n.getChains() ){
                    if(chain.getOriginId().equals(entry.getKey())){
                        allTTPs.addAll(chain.getTtps());
                    }
                }
            }
            System.out.println("TTP-Kette: " + allTTPs);
        }
    }

    /**
     * Soll verschiedene Szenarios erkennen
     * @return Liste mit allen Szenarios
     */
    public List<List<Node>> detectScenarios (){
        List<List<Node>> scenarios = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        for(String nodeId : hsgNodes.keySet()){
            if(!visited.contains(nodeId)){
                List<Node> scenario = new ArrayList<>();
                traverse(nodeId, visited,scenario);
                scenarios.add(scenario);
            }
        }
        return scenarios;
    }

    /**
     * Durchläuft den HSG
     * @param nodeId Startknoten
     * @param visited Liste, bereits besuchter Knoten
     * @param scenario Szenario
     */
    private void traverse(String nodeId, Set<String> visited, List<Node> scenario){
        Queue<String> queue = new LinkedList<>();
        queue.add(nodeId);

        while (!queue.isEmpty()){
            String current = queue.poll();
            if(!visited.contains(current)){
                visited.add(current);
                scenario.add(hsgNodes.get(current));

                for(String dstId: hsgEdges.getOrDefault(current, Collections.emptySet())){
                    if(!visited.contains(dstId)){
                        queue.add(dstId);
                    }

                }
                for(String srcId: incomingEdges.getOrDefault(current, Collections.emptySet())){
                    if(!visited.contains(srcId)){
                        queue.add(srcId);
                    }
                }
            }
        }
    }

    /**
     * Sammelt alle ProvGraph Knoten mit gefundenen TTPs
     * in der hsgNodes Map und erstellt zu befüllende Kanten
     */
    private void collectTTPNodes() {
        for(Node n : graph.getNodes().values()){
            //Nur Knoten mit TTPs
            if(!n.getTtps().isEmpty()){
                hsgNodes.put(n.getHashId(), n);
                hsgEdges.put(n.getHashId(), new HashSet<>());
            }
        }
        System.out.println("[HSG] "+ hsgNodes.size()+ " potenzielle Knoten gefunden");
    }

    /**
     * Verbindet die Knoten in hsgNodes miteinander,
     * wenn der PathFactor stimmt
     */
    private void connectNodes(){
        List<String> nodeIds = new ArrayList<>(hsgNodes.keySet());
        //Für jeden Quellknoten
        for(int i =0; i< nodeIds.size(); i++){
            String srcId = nodeIds.get(i);
            Map<String,Integer> pathFactors = pfEngine.computePfFrom(srcId);

            //Für jeden Zielknoten
            for (int j=0; j<nodeIds.size(); j++){
                if(i!=j){
                    String dstId= nodeIds.get(j);
                    int pf = pathFactors.getOrDefault(dstId, Integer.MAX_VALUE);
                    //Wenn PF passt Kante erzeugen
                    if(pf <= TTP.PF_THRESHOLD){
                        hsgEdges.get(srcId).add(dstId);
                        if(incomingEdges.containsKey(dstId)){
                            incomingEdges.get(dstId).add(srcId);
                        } else {
                            Set<String> incoming = new HashSet<>();
                            incoming.add(srcId);
                            incomingEdges.put(dstId,incoming);
                        }
                    }
                }
            }
        }
    }


    /**
     * Gibt den HSG auf der Konsole aus
     * Gibt es Möglichkeiten den richtig krass darzustellen?
     * -> Wenn Zeit nachschauen
     */
    private void printHSG(){
        for(Map.Entry<String, Set<String>> entry : hsgEdges.entrySet()){
            Node srcNode = hsgNodes.get(entry.getKey());
            if(entry.getValue().isEmpty()){
                System.out.println(printNode(srcNode)+ "---> [LEER]");
                continue;
            }
            for (String dstId : entry.getValue()){
                Node dstNode = hsgNodes.get(dstId);
                System.out.println(printNode(srcNode) + "-->"+ printNode(dstNode));
            }

        }
    }

    //Hilfsmethode für einheitliche Knoten ausgabe
    private String printNode(Node node){
        return "[" +node.getName() + " ("+ node.getTtps()+ ")]";
    }

    public void printScenarios(List<List<Node>> scenarios){
        int count = 0;

        for(List<Node> scenario : scenarios){
            count++;
            System.out.println("\n---Scenario "+ count + "---");
            System.out.println("Beteiligte Knoten:" + scenario.size());

            Set<String> ttps = new LinkedHashSet<>();
            for(Node n: scenario){
                ttps.addAll(n.getTtps());

            }
            System.out.println("Erkannte TTPs: "+ ttps);

            for (Node n: scenario){
                System.out.println(" "+ n.getName() + " --> TTPs: "+ n.getTtps());
            }
        }

    }
}
