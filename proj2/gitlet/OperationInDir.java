/** This class represents how to hash a file
 * the context of a file, and the name of a file in gitlet
 * The methods below are shared by Object, referenceSubDir.
 */
package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.InputStream;

public class OperationInDir extends Utils implements Serializable {
    /**
     * objects have been hashed, string is the type of the object, for example:
     * blob. the strings in sets are shaID about the specific operations. For set,
     * the actual dynamic. type is linkedHashSet, so we can trace the sequence to
     * add commit, tree or branch etc.
     */
    protected HashMap<String, Set<String>> addedInSep = new HashMap<>();
    /**
     * objects have been loaded in the cache. take the specific shaID to get the
     * files have been loaded
     */
    protected HashMap<String, Object> objectCache = new HashMap<>();
    /**
     * Current Working Directory.
     */
    private File workingDir;
    /**
     * whether the SerialVersionUID is same.
     */
    private boolean match;
    /**
     * take the SHA value to get the byte form file or object.
     */
    protected HashMap<String, String> serialItem = new HashMap<>();

    /**
     * This is a constructor that takes in the directory we will use and creates a
     * hashed object
     * 
     * @param dir the directory we will hash or files in it we will hash.
     */
    public OperationInDir(String dir) {
        this.workingDir = new File(dir);
    }

    /**
     * take the Object Item or Reference Item, and add the specific SHAId to the
     * folder.
     * 
     * @param objName, this is the object of the item we want to add in.
     */
    public void add(String objName, String hashId) {
        if (!addedInSep.containsKey(objName)) {
            Set<String> trackedSHA = new LinkedHashSet<>();
            addedInSep.put(objName, trackedSHA);
        } else {
            if (this.contains(hashId)) {
                throw new IllegalArgumentException("This object has already in the directory");
            } else {
                addedInSep.get(objName).add(hashId);
            }
        }
    }

    /**
     * This method check whether the hashObject contains a file
     * 
     * @param shaID
     * @return
     */
    public boolean contains(String shaID) {
        for (Set<String> sha : this.addedInSep.values()) {
            Iterator<String> iterator = sha.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().equals(shaID)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method takes the class type and check whether there exists the shaID
     * 
     * @param obj
     * @param shaID
     * @return
     */
    public boolean contains(String obj, String shaID) {
        Set<String> sha = this.addedInSep.get(obj);
        if (sha == null) {
            return false;
        }
        return sha.contains(shaID);
    }

    public Object get(String objType, String objName) throws IOException, ClassNotFoundException {
        String objShaValue = this.serialItem.get(objType);
        Object objGet = this.objectCache.get(objShaValue);
        if (objGet == null) {
            return this.load(objType, objName);
        } else {
            return objGet;
        }
    }

    /**
     * This method remove a object by the objectName in the current directory print
     * "File does not exist." if file passed doesn't in the current Directory.
     * 
     * @param objName
     * @param objValue
     */
    public void remove(String objName, String objValue) {
        try {
            String dirPathString = workingDir.getAbsolutePath();
            Path dirPath = Paths.get(dirPathString);
            Path filePath = dirPath.resolve(objName);
            if (!this.serialItem.containsValue(objName) || !this.objectCache.containsKey(objName)) {
                throw new IllegalArgumentException("File does not exist.");
            }
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method takes the fileName and the class of file, for example, commit and
     * convert it to the actual content.
     * 
     * @param objType
     * @param objectName
     * @return
     * @throws IOException
     */

    public Object load(String objType, String objectName) throws IOException,
            ClassNotFoundException {
        Path objectPath = Path.of(this.getWorkingDir().getAbsolutePath()).resolve(objectName);
        InputStream loadedOne = Files.newInputStream(objectPath);
        ObjectInputStream ObjectTransform = new ObjectInputStream(loadedOne);
        Object objGet;
        objGet = ObjectTransform.readObject();
        loadedOne.close();
        ObjectTransform.close();
        this.objectCache.put(objectName, objGet);
        return objGet;
    }

    /**
     * This method returns workingDir.
     * 
     * @return
     */
    public File getWorkingDir() {
        return this.workingDir;
    }
}
