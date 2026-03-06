package Database.Graph;

public class File extends Node{

    private String path;

    public File(long uuid, long nodeIndex, String path){
        super(uuid,nodeIndex);
        setPath(path);
    }

    public void setPath(String path) {
        this.path = path;
    }
    public String getPath() {
        return path;
    }
}
