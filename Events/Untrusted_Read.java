package Events;

public class Untrusted_Read extends TTP{

    public Untrusted_Read(){
        setSeverity('M');
        setType(new READ());
    }
}
