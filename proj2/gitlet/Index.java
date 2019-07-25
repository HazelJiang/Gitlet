package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.TreeMap;

public class Index extends Utils implements Serializable {
    private static final long serialVersionUID = -8339216273921535654L;
    /**
     * the key is the filename and the value is this contents' shaID, so that we can track different
     * versions of file with the same name.
     */
    private HashMap<String, String> blob;
    /**
     * The actual stage area we use to add items to the stage area, and the key is the objectName for ob
     * and the value is the shaID for this object, based on the content of the obj
     */
    private TreeMap<String, String> addStage;
    /**
     * This actual stage area we use to delete items
     */
    private TreeMap<String, String> removeStage;

    /**
     * This constructor creates a new Index File, that make a snapshot for the staging area
     * blobs is the shaId based on the content of files, and fileName
     * addStage is what we have tracked in the staging area
     * removeStage is what we decide to untrack or delete in staging area.
     */
    public Index() {
        this.blob = new HashMap<>();
        this.addStage = new TreeMap<>();
        this.removeStage = new TreeMap<>();
    }

    /**
     * This method is to add a file and its corresponding shaValue to the staging area
     * and put the old one to the removeStage
     * @param fileName
     * @param shaValue
     */
    public void add(String fileName, String shaValue) {
        if(this.addStage.get(fileName) == null) {
            addStage.put(fileName, shaValue);
        } else if (this.addStage.get(fileName) != null && this.addStage.get(fileName) != shaValue) {
            removeStage.put(fileName, addStage.get(fileName));
            addStage.put(fileName, shaValue);
        }
    }

    /**
     * This method replace a file with the new SHA value in the blobs
     * @param fileName
     * @param shaValue
     * @param onStage
     * @throws IllegalArgumentException
     */
    public void checkout(String fileName, String shaValue, boolean onStage) throws IllegalArgumentException {
        if (!onStage) {
            throw new IllegalArgumentException("File does not exist in that commit.");
        } else {
            this.blob.put(fileName, shaValue);
            this.addStage.remove(fileName);
            this.removeStage.remove(fileName);
        }

    }
    /**
     * clear the staging area.
     */
    public void rmCached() {
        this.addStage.clear();
        this.removeStage.clear();
    }

    /**
     * this method takes the filename, and a boolean to make sure we seperate it to two dif operations
     * If the file is tracked by the current commit, delete the file from the working directory,
     * unstage it if it was staged, and mark the file to be untracked by the next commit.
     * If the file isn’t tracked by the current commit but it is staged,
     * unstage the file and do nothing else (don’t remove the file!).
     * @param fileName
     * @param onStage
     */
    public void rm(String fileName, boolean onStage) {
        if (!blob.containsKey(fileName) || !onStage) {
            throw new IllegalArgumentException("No reason to remove the file.");
        }
        removeStage.put(fileName, blob.get(fileName));
        blob.remove(fileName);
        addStage.remove(fileName);
    }

    public boolean checkChanged() {
        return removeStage.size() != addStage.size();
    }

    public HashMap<String, String> getBlob() {
        return this.blob;
    }

    public TreeMap<String, String> getAddStaged() {
        return this.addStage;
    }



    /*add();
    commit();
    rm();
    checkout();
    *merge();//finish later
    reset();*/


}
