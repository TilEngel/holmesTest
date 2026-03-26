package hsg;

import Database.Graph.Node;
import provenanceGraph.ProvGraph;

import java.util.*;

/**
 * Stellt den HSG nach außen dar.
 * Kann erzeugt und ausgegeben werden, sonst noch nicht so viel
 */
public class HSGBuilder {
    private final ProvGraph graph;

    public HSGBuilder(ProvGraph graph){
        this.graph = graph;
    }

    /**
     * Sammelt alle Knoten, an denen TTPs gefunden wurden.
     * Verbindet sie unter Berücksichtigung des PathFactors.
     * Gibt Verbindungen auf Konsole aus.
     * @return Sammlung an Szenarien (HSGs)
     */
    public Map<String,List<Node>> constructHSG(){

        Map<String, List<Node>> scenarios = new HashMap<>();

        for(Node node: graph.getNodes().values()){
            //Für jede Kette aller Knoten
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

        return scenarios;
    }

    /**
     * Gibt alle Szenarien aus
     * @param scenarios Auszugebene Szenarien
     */
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
