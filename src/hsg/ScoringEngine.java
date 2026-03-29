package hsg;

import Database.Graph.Node;
import events.ttps.TTP;

import java.util.*;

import static java.lang.Math.pow;

public class ScoringEngine {
    public static final int ALARM_THRESHOLD = 100;
    Map<String, List<Node>> scenarios;

    public ScoringEngine(Map<String,List<Node>> scenarios){
        this.scenarios =scenarios;
    }

    /**
     * Liefert sortierte Liste der Szenarien mit ihren Threat-Scores
     * @return Bewertete Szenarien
     */
    public List<Map.Entry<Double,List<Node>>> scoreSzenarios(){
        List<Map.Entry<Double,List<Node>>> rankedScenarios = new ArrayList<>(); //Sortierte Liste mit Scores und Szenarien

        for (Map.Entry<String, List<Node> > entry : scenarios.entrySet()) {
            List<Node> involved = entry.getValue();
            double score= computeScore(involved);

            rankedScenarios.add(Map.entry(score, involved));
            System.out.println("[RESULT] Szenario Score: " + score);
            if(score >= ALARM_THRESHOLD){
                System.out.println("[ALARM] GRENZWERT ÜBERSCHRITTEN!! --------");
            }
        }
        //Nach Score (DESC) sortieren
        rankedScenarios.sort((a,b) -> Double.compare(b.getKey(),a.getKey() ));
        return  rankedScenarios;
    }

    /**
     * Berechnet Threat-Score eines Scenarios
     * @param involved Szenario
     * @return Score
     */
    private double computeScore(List<Node> involved){
        double score = 1.0;
        for(int i= 0; i< involved.size(); i++){
            double weight = (10.0 + i+1) / 10.0;
            double severity = getHighestSeverityValue(involved.get(i));
            score *= pow(severity, weight);
        }
        return  score;
    }

    /**
     * Liefert höchsten Severity-Value
     * (mehrere TTPs pro Phase möglich)
     * @param node
     * @return
     */
    private int getHighestSeverityValue(Node node){
        int highest = 0;
        for(TTP ttp : node.getTTPObjects()){
            int temp = getSeverityValue(ttp);
            if(temp> highest){
                highest = temp;
            }
        }
        return highest;
    }

    /**
     * Liefert numerischen Wert für Severities
     * (über so eine Methode, damit Werte zentral änderbar sind)
     * @param ttp entsprechendes TTP
     * @return Severity-Wert
     */
    private int getSeverityValue(TTP ttp){
        char severity = ttp.getSeverity();

        if(severity == 'L'){
            return 2;
        }
        if(severity == 'M'){
            return  6;
        }
        if(severity== 'H'){
            return  8;
        }else{
            return 10;
        }
    }
}
