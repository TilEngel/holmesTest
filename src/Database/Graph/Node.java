package Database.Graph;

import events.ttps.TTP;
import hsg.TTPChain;

import java.util.*;

public abstract class Node {
    private String uuid;
    private long nodeIndex;

    private String hashId;

    private final Set<String> ttps = new HashSet<>();
    private final Set<TTP> ttpObjects  = new HashSet<>();
    private final List<TTPChain> chains = new ArrayList<>();

    public Node(String uuid, long nodeIndex, String hashId) {
        setNodeIndex(nodeIndex);
        setUuid(uuid);
        setHashId(hashId);
    }

    public void setUuid(String uuid){
        this.uuid = uuid;
    }
    public void setNodeIndex(long nodeIndex) {
        this.nodeIndex = nodeIndex;
    }
    public String getUuid() {
        return uuid;
    }

    public long getNodeIndex() {
        return nodeIndex;
    }

    public void setHashId(String hashId){
        this.hashId= hashId;
    }

    public String getHashId(){
        return hashId;
    }

    /**
     * TTP dem Knoten hinzufügen
     * @param ttp hinzuzufügendes TTP
     */
    public void addTTP(TTP ttp){
        ttps.add(ttp.getName());
        ttpObjects.add(ttp);
    }

    /**
     * Schaut, ob TTP bei dem Knoten eingetragen ist
     * @param ttp Entsprechendes TTP
     * @return true, wenn TTP bereits eingetragen ist, false sonst
     */
    public boolean hasMatchedTTP(TTP ttp){
        return ttps.contains(ttp.getName());
    }
    public Set<String> getTtps(){
        return ttps;
    }
    public void addChain(TTPChain chain){
        chains.add(chain);
    }

    public List<TTPChain> getChains() {
        return Collections.unmodifiableList(chains);
    }

    /**
     * Prüft, ob identische Chain bereits vorhanden ist
     * @param chain TTPChain
     * @return true, wenn bereits vorhanden
     */
    public boolean hasChain(TTPChain chain){
        for(TTPChain c: chains){
            if(chain.isDuplicateOf(c)){
                return true;
            }
        }
        return false;
    }
    /**
     * Liefert den Namen des Knotens, je nach Art unterschiedlich
     * (Subject: Name des Commands , File: Pfadname, Netflow: Source Port)
     * Methode hilft bei Test-Ausgaben
     * @return Name des Knotens
     */
    public abstract String getName();
    public Set<TTP> getTTPObjects(){
        return ttpObjects;
    }
}
