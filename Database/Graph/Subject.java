package Database.Graph;

public class Subject extends Node{
    private String path;

    private String cmd;


    public Subject (long uuid, long nodeIndex, String path, String cmd){
        super(uuid, nodeIndex);
        setPath(path);
        setCmd(cmd);
    }

    public void setPath(String path){
        this.path = path;
    }
    public void setCmd(String cmd){
        this.cmd = cmd;
    }

    public String getPath() {
        return path;
    }

    public String getCmd(){
        return cmd;
    }
}
