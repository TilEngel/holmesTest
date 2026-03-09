package Database.Graph;

public class File extends Node{

    private String path;

    public File(String uuid, long nodeIndex, String path){
        super(uuid,nodeIndex);
        setPath(path);
    }

    @Override
    public String getName(){
        return "(File) "+ getPath();
    }

    public void setPath(String path) {
        this.path = path;
    }
    public String getPath() {
        return path;
    }
}


