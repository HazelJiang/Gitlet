package gitlet;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Commit extends ObjectInDir implements Serializable {
    private static final long serialVersionUID = -4029158655088522902L;
    private String parent;
    private Date time;
    private HashMap<String, String> blobs;
    private String message;

    public Commit(String message, Date date, String parent, HashMap<String, String> blobs) {
        if (message == null || message.equals("")) {
            throw new IllegalArgumentException("Please enter a commit message");
        }
        this.message = message;
        this.time = date;
        this.blobs = blobs;
        this.parent = parent;
    }

    public Commit(String message, Date date) {
        this(message, date, "", new HashMap<String, String>());
    }
    @Override
    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String currentTime = dateFormat.format(date);
        return "===\n" + "Commit" + Utils.sha1(this) + "\n" + currentTime + "\n" + this.message;
    }

    public boolean containKeys(String fileName) {
        return this.blobs.containsKey(fileName);
    }

    public boolean containsValue (String fileContentShaID) {
        return this.blobs.containsValue(fileContentShaID);
    }

    /**
     * This method takes a file name and get this corresponding shaID of hashed content.
     * @return
     */
    public String get(String fileName) {
        return this.blobs.get(fileName);
    }

    /**
     * put a new file into the blob
     * @param fileName
     * @param hashValue
     * @return
     */
    public String put(String fileName, String hashValue) {
        return this.blobs.put(fileName, hashValue);
    }

    /**
     * remove a file from the blob
     * @param fileName
     * @return
     */
    public String remove(String fileName) {
        return this.blobs.remove(fileName);
    }

    /**
     * put several files into the commit
     * @param addBlob
     */
    public void putAll(HashMap<String, String> addBlob) {
        this.blobs.putAll(addBlob);
    }

    /**
     * remove all the elements in the blobs
     */
    public void clear() {
        this.blobs.clear();
    }

}
