package Database.Graph;

public class Netflow extends Node{
    private String srcAddr;

    private String srcPort;

    private String dstAddr;

    private String dstPort;

    public Netflow(long uuid, long nodeIndex, String srcAddr, String srcPort, String dstAddr, String dstPort){
        super(uuid, nodeIndex);
        setSrcAddr(srcAddr);
        setSrcPort(srcPort);
        setDstAddr(dstAddr);
        setDstPort(dstPort);
    }

    public void setSrcAddr(String srcAddr){
        this.srcAddr= srcAddr;
    }
    public void setSrcPort(String srcPort){
        this.srcPort= srcPort;
    }
    public void setDstAddr(String dstAddr){
        this.dstAddr= dstAddr;
    }
    public void setDstPort(String dstPort){
        this.dstPort= dstPort;
    }

    public String getSrcAddr(){
        return srcAddr;
    }

    public String getSrcPort(){
        return srcPort;
    }
    public String getDstAddr(){
        return dstAddr;
    }
    public String getDstPort(){
        return dstPort;
    }

}
