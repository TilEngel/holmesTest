package Database.Graph;

import Events.TTP;

import java.util.HashSet;
import java.util.Set;

public abstract class Node {
    private String uuid;
    private long nodeIndex;

    private String hashId;

    private final Set<TTP> ttps = new HashSet<>();

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
        ttps.add(ttp);
    }

    /**
     * Schaut, ob TTP bei dem Knoten eingetragen ist
     * @param ttp Entsprechendes TTP
     * @return true, wenn TTP bereits eingetragen ist, false sonst
     */
    public boolean hasMatchedTTP(TTP ttp){
        return ttps.contains(ttp);
    }

    /**
     * Liefert den Namen des Knotens, je nach Art unterschiedlich
     * (Subject: Name des Commands , File: Pfadname, Netflow: Source Port)
     * Methode hilft bei Test-Ausgaben
     * @return Name des Knotens
     */
    public abstract String getName();
}
