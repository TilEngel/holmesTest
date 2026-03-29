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

            //TTPs
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


    /**
     * Gibt die Szenarien sortiert nach Threat-Score mit Threat-Score aus
     * @param rankedScenarios Bewertete Szenarien (durch ScoringEngine.scoreScenarios)
     */
    public void printRankedScenarios(List<Map.Entry<Double,List<Node>>> rankedScenarios){
        System.out.println("\n++Szenarien (Sortiert absteigend nach Bedrohlichkeit)++ \n");
        int count = 0;
        for (Map.Entry<Double, List<Node>> entry : rankedScenarios) {
            count++;
            List<Node> involved = entry.getValue();
            double score = entry.getKey();
            String origin = involved.get(0).getChains().get(0).getOriginId();

            System.out.println("\n Szenario " + count);
            System.out.println("Threat-Score: " + score);
            if(score >= ScoringEngine.ALARM_THRESHOLD){
                System.out.println("\nGEFAHR\n");
            }
            System.out.println("Beteiligte Knoten: " + involved.size());

            //TTPs des Szenarios sammeln
            Set<String> allTTPs = new LinkedHashSet<>();
            for (Node n : involved) {
                for (TTPChain chain : n.getChains()) {
                    if (chain.getOriginId().equals(origin)) {
                        allTTPs.addAll(chain.getTtps());
                    }
                }
            }
            System.out.println("TTP-Kette: " + allTTPs);
        }
    }
}
