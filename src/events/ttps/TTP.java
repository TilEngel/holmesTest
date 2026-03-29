package events.ttps;

import Database.Graph.Edge;
import Database.Graph.Node;
import java.util.List;

import events.EventType;
import events.Prerequisite;
import hsg.PathFactorEngine;
import provenanceGraph.ProvGraph;


public abstract class TTP {
    public static final int PF_THRESHOLD = 3;

    private char severity;

    private EventType.Type type;

    private List<Prerequisite> prerequisites;
    static PathFactorEngine pfEngine; //"singleton" PFEngine -> ressourcenschonend

    /**
     * Prüft, ob alle Bedingungen für das TTP gelten.
     * Sehr ineffizient, da unnötig viele PF-Berechnungen durchgeführt werden.
     * Will mich noch nicht final davon trennen.
     * @param edge Kante
     * @param graph Provenance-Graph
     * @return true, wenn Verhalten festgestellt wird
     */
    public abstract boolean matchesOld(Edge edge, ProvGraph graph);

    /**
     * Prüft, ob das Geschehen an den Knoten einer Kante zu dem entsprechenden
     * TTP passt
     * @param edge Zu untersuchende Kante
     * @param graph Provenance-Graph
     * @return true, wenn TTP erkannt wird
     */
    public abstract boolean matches(Edge edge, ProvGraph graph);

    //gibt TTP-Name zurück, welcher in Node.ttps gespeichert wird
    public abstract String getName();
    public abstract String getPhase();
    /**
     * Prüft für alle Prärekonditionen des entsprechenden
     * TTPs, ob sie eingehalten werden
     * @param edge Die Kante dessen Knoten geprüft wird
     * @param graph Provenance-Graph
     * @return true, wenn alle Bedingungen eingehalten werden, sonst false
     */
    protected boolean prerequisitesMet(Edge edge, ProvGraph graph ){
        for(Prerequisite p : prerequisites){
            if(!p.evaluate(edge, graph)){
                return false;
            }
        }
        return true;
    }

    /**
     * Prüft, ob der Knoten nach einem potenziellen
     * Initial Compromise folgt, unter Prüfung des PFs
     *
     * Wird nur von matchesOld() verwendet
     * @param target Zielknoten
     * @param graph Provenance-Graph
     * @return true, wenn Bedingung erfüllt, false sonst
     */
    boolean hasInitialCompromiseAncestor(Node target, ProvGraph graph){
        Untrusted_Read uR = new Untrusted_Read();
        Make_Mem_Exec mME = new Make_Mem_Exec(pfEngine);
        Untrusted_File_Exec uFE = new Untrusted_File_Exec(pfEngine);

        for(Node candidate: graph.getNodes().values()){
            if(candidate.hasMatchedTTP(uR)|| candidate.hasMatchedTTP(mME) || candidate.hasMatchedTTP(uFE)){
                if(pfEngine.isInPfThreshold(candidate.getHashId(),target.getHashId(), PF_THRESHOLD)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Prüft, ob ein Internal_Recon mit
     * PathFactor<= PF_THRESHOLD existiert.
     *
     * Wird nur von matchesOld() verwendet
     * @param target Zielknoten
     * @param graph Provenance-Graph
     * @return true, wenn Bedingungen erfüllt, false, wenn nict
     */
    boolean hasInternalReconAncestor(Node target, ProvGraph graph){
        Sensitive_Command sc = new Sensitive_Command(pfEngine);

        for(Node candidate : graph.getNodes().values()){
            if(candidate.hasMatchedTTP(sc)){
                if(pfEngine.isInPfThreshold(candidate.getHashId(), target.getHashId(), PF_THRESHOLD)){
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Prüft, ob ein Untrusted Read mit
     * PathFactor<= PF_THRESHOLD existiert.
     *
     * Wird nur von matchesOld() verwendet
     * @param target Zielknoten
     * @param graph Provenance-Graph
     * @return true, wenn Bedingungen erfüllt, false, wenn nict
     */
    boolean hasUntrustedReadAncestor(Node target, ProvGraph graph){
        Untrusted_Read uR = new Untrusted_Read();

        for(Node candidate : graph.getNodes().values()){
            if(candidate.hasMatchedTTP(uR)){
                if(pfEngine.isInPfThreshold(candidate.getHashId(), target.getHashId(), PF_THRESHOLD)){
                    return true;
                }
            }
        }
        return false;
    }

    EventType.Type getType(){
        return type;
    }
    public char getSeverity(){
        return severity;
    }

    protected void setType(EventType.Type type){
        this.type= type;
    }
    protected void setSeverity(char severity){
        this.severity = severity;
    }

    protected void setPrerequisites(List<Prerequisite> prerequisites){
        this.prerequisites = prerequisites;
    }

}
