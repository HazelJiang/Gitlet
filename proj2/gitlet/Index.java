package gitlet;

import java.io.Serializable;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashSet;

public class Index extends Utils implements Serializable {
    private static final long serialVersionUID = -8339216273921535654L;
    /**
     * The actual stage area we use to add items to the stage area, and the key is the.
     * objectName for ob
     * and the value is the shaID for this object, based on the content of the obj
     */
    private TreeMap<String, String> addStage;
    /**
     * This actual stage area we use to delete items
     */
    private Set<String> removeStage;

    public TreeMap<String, String> getAddStage() {
        return addStage;
    }

    public void setAddStage(TreeMap<String, String> addStage) {
        this.addStage = addStage;
    }

    public Set<String> getRemoveStage() {
        return removeStage;
    }

    public void setRemoveStage(Set<String> removeStage) {
        this.removeStage = removeStage;
    }

    /**
     * This constructor creates a new Index File, that make a snapshot for the staging area
     * blobs is the shaId based on the content of files, and fileName
     * addStage is what we have tracked in the staging area
     * removeStage is what we decide to untrack or delete in staging area.
     */
    public Index() {
        this.addStage = new TreeMap<>();
        this.removeStage = new HashSet<>();
    }

    /**
     * This method is to add a file and its corresponding shaValue to the staging area
     * and put the old one to the removeStage
     * @param fileName
     * @param shaValue
     */
    public void add(String fileName, String shaValue) {
        addStage.put(fileName, shaValue);
    }

    /**
     * clear the staging area.
     */
    public void rmCached() {
        this.addStage.clear();
        this.removeStage.clear();
    }

    /**
     * Removes a file.
     * Specifically, if the files is already staged, remove it from the stage.
     * Otherwise, mark the file to be untracked by the next commit.
     * @param fileName
     */
    public void remove(String fileName) {
        if (staged(fileName)) {
            addStage.remove(fileName);
        } else {
            removeStage.add(fileName);
        }
    }

    public boolean checkChanged() {
        return removeStage.size() != addStage.size();
    }

    public boolean staged(String fileName) {
        return addStage.containsKey(fileName);
    }

}
