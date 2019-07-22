package gitlet;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Commit<T> extends OperationInDir<T> {
    private static final long serialVersionUID = -4029158655088522902L;
    private String shaID;
    private LocalDateTime time;
    private LinkedHashMap<String, String> commitHistory;
    private HashMap<String, String> blobs;


     
}
