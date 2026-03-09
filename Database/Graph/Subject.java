package Database.Graph;

public class Subject extends Node{
    private String path;

    private String cmd;


    public Subject (String uuid, long nodeIndex, String path, String cmd){
        super(uuid, nodeIndex);
        setPath(path);
        setCmd(cmd);
    }

    @Override
    public String getName(){
        return "(Sub) "+ getCmd();
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

