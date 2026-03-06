package Database.Graph;

abstract class Node {
    private long uuid;
    private long nodeIndex;

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
