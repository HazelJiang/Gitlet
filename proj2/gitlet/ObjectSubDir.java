package gitlet;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ObjectSubDir<T extends Serializable> implements Iterable<T> {
    private HashMap<String, Serializable> filePut;
    private Path workingDir;
    private LinkedHashMap


    /** construct a object subdirectory in the gitlet repo
     * @param file the directory user types in
     */
    public ObjectSubDir(String file) {
        this.workingDir = Paths.get(file);
        filePut = new HashMap<>();

    }
    /** This method is used to load a file into the object directory
     * @param file filename
     */
    public void load(String file) {
        Path filePath = this.workingDir.resolve(file);


    }
}
