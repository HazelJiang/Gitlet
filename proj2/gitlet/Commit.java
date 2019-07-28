package gitlet;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Represents all information in a commit.
 */
public class Commit extends ObjectInDir {
    private static final long serialVersionUID = -4029158655088522902L;
    private String parent;
    private Date time;
    private HashMap<String, String> blobs;
    private String message;

    /**
     * Constructor for Commit.
     * @param message the commit message
     * @param date the date of this commit
     * @param parent the parent commit of this commit to get the original file SHA values
     * @param repo the current repository in order to get information about the parent commit
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Commit(String message, Date date, String parent, Repository repo)
            throws IOException, ClassNotFoundException {
        this.message = message;
        this.time = date;
        this.parent = parent;
        if (parent != null) {
            Commit parentCommit = (Commit) repo.getObjectDir().get(parent);
            blobs = new HashMap<>(parentCommit.blobs);
        } else {
            blobs = new HashMap<>();
        }
    }

    public Commit(String message, Date date) throws IOException, ClassNotFoundException {
        this(message, date, null, null);
    }

    /**
     * Merge the changes from the parent commit stored in {@code index} to this commit.
     * @param index the index area since the last commit.
     */
    public void mergeIndex(Index index) {
        for (Map.Entry<String, String> entry: index.getAddStage().entrySet()) {
            blobs.put(entry.getKey(), entry.getValue());
        }
        for (String fileName: index.getRemoveStage()) {
            if (!blobs.containsKey(fileName)) {
                continue;
            }
            blobs.remove(fileName);
        }
    }

    @Override
    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String currentTime = dateFormat.format(date);
        return "===\n" + "Commit " + this.sha() + "\n" + currentTime + "\n" + this.message;
    }

    public boolean containsFile(String fileName) {
        return this.blobs.containsKey(fileName);
    }

    public boolean containsValue(String fileContentShaID) {
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

    public HashMap<String, String> getBlob() {
        return this.blobs;
    }

    public String getClassName() {
        return "Commit";
    }

    public String getParent() {
        return this.parent;
    }

    public String getMessage() { return this.message; }

    public boolean find(String message) {
        return this.message.contains(message);
    }

    public Commit getParentCommit(Repository rep)
            throws IOException, ClassNotFoundException {
        if (parent == null) {
            return null;
        }
        return (Commit) rep.getObjectDir().get(parent);
    }

    public String sha() {
        String sha = message;
        sha += time.toString();
        ArrayList<String> shas = new ArrayList(blobs.values());
        Collections.sort(shas);
        for (String fileSHA: shas) {
            sha += fileSHA;
        }
        return Utils.sha1(sha);
    }

}
