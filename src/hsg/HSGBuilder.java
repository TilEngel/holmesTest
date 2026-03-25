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
        printScenarios(scenarios);
    }

    public void printScenarios(Map<String,List<Node>> scenarios) {
        int count = 0;
        for (Map.Entry<String, List<Node>> entry : scenarios.entrySet()) {
            count++;
            Node origin = graph.getNode(entry.getKey());
            List<Node> involved = entry.getValue();

            System.out.println("\n Szenario " + count);
            System.out.println("Ursprung: " + origin.getName());
            System.out.println("Beteiligte Knoten: " + involved.size());

            Set<String> allTTPs = new LinkedHashSet<>();
            for (Node n : involved) {
                for (TTPChain chain : n.getChains()) {
                    if (chain.getOriginId().equals(entry.getKey())) {
                        allTTPs.addAll(chain.getTtps());
                    }
                }
            }
            System.out.println("TTP-Kette: " + allTTPs);
        }
    }
}
