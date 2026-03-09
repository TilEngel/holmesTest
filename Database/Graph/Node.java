package Database.Graph;

public abstract class Node {
    private String uuid;
    private long nodeIndex;

    public Node(String uuid, long nodeIndex) {
        setNodeIndex(nodeIndex);
        setUuid(uuid);
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

    public abstract String getName();
}
