package Database.Graph;

public abstract class Node {
    private long uuid;
    private long nodeIndex;

    public Node(long uuid, long nodeIndex) {
        setNodeIndex(nodeIndex);
        setUuid(uuid);
    }

    public void setUuid(long uuid){
        this.uuid = uuid;
    }
    public void setNodeIndex(long nodeIndex) {
        this.nodeIndex = nodeIndex;
    }
    public long getUuid() {
        return uuid;
    }

    public long getNodeIndex() {
        return nodeIndex;
    }
}
