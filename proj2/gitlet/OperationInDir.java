/** This class represents how to hash a file, the context of a file, and the name of a file in gitlet
 * The methods below are shared by Object, referenceSubDir.
 */
package gitlet;

import java.beans.XMLDecoder;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class OperationInDir<T> extends Utils implements Serializable {
    /**
     * objects have been hashed, string is the type of the object, for example: blob
     * the strings in sets are shaID about the specific operations. For set, the actual dynamic
     * type is linkedHashSet, so we can trace the sequence to add commit, tree or branch etc.
     */
    protected HashMap<Object, Set<String>> AddedInSep = new HashMap<>();
    /**
     * objects have been loaded in the cache, take the specific shaID to get the files have been loaded
     */
    protected HashMap<String, Object> fileCache = new HashMap<>();
    /**
     * Current Working Directory.
     */
    private File workingDir;
    /**
     * whether the SerialVersionUID is same.
     */
    private boolean match;
    /**
     * take the SHA value to get the byte form file or object
     */
    protected HashMap<String, byte[]> serializedItem = new HashMap<>();
    /**
     * This is a constructor that takes in the directory
     * we will use and creates a hashed object
     * @param Dir the directory we will hash or files in it we will hash.
     */
    public OperationInDir(String Dir) {
        this.workingDir = new File(Dir);
    }

    /** take the Object Item or Reference Item, and add the specific SHAId to the folder
     * @param obj, this is the object of the item we want to add in.
     */
    public void add(Object obj , String hashId) {
        if (!AddedInSep.containsKey(obj)) {
            Set<String> trackedSHA = new LinkedHashSet<>();
            AddedInSep.put(obj, trackedSHA);
        } else {
           if (this.contains(hashId)) {
               throw new IllegalArgumentException("This object has already in the directory");
           } else {
               AddedInSep.get(obj).add(hashId);
           }
        }
    }
    /** This method check whether the hashObject contains a file
     * @param shaID
     * @return
     */
    public boolean contains(String shaID) {
        for (Set<String> SHA  : this.AddedInSep.values()) {
            Iterator<String> iterator = SHA.iterator();
            while(iterator.hasNext()) {
                if (iterator.next().equals(shaID)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** This method takes the class type and check whether there exists the shaID
     * @param className
     * @param shaID
     * @return
     */
    public boolean contains(String className, String shaID) {
        Set<String> SHA = this.AddedInSep.get(className);
        if (SHA == null) {
            return false;
        }
        return SHA.contains(shaID);
    }
    /** This method takes the filepath that saved in serializeItem hashmap and get the serialized
     * file content.
     * @param filePath
     * @return
     * @throws IllegalArgumentException
     */
    public byte[] getFileContent(String filePath) throws IllegalArgumentException {
        byte[] serialFile = this.serializedItem.get(filePath);
        return serialFile;
    }

    /**
     * This method takes a SHA id with specific type to check whether it exist and return this object.
     * @param className
     * @param SHAid
     * @return
     * @throws IllegalArgumentException
     */
    public byte[] getFileContent(String className, String SHAid) throws IllegalArgumentException {
        if (AddedInSep.get(className) == null) {
            throw new IllegalArgumentException("this object doesn't exist");
        } else {
            Set<String> SHA = this.AddedInSep.get(className);
            Iterator<String> iterator = SHA.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().equals(SHAid)){
                    byte[] content = this.serializedItem.get(SHAid);
                    return content;
                }
            }
        }
        return  null;
    }

    /** This method remove a file by the fileName in the current directory
     * print "File does not exist." if file passed doesn't in the current Directory.
     * @param fileName
     */
    public void remove(String fileName) {
        try {
            String dirPathString = workingDir.getAbsolutePath();
            Path dirPath = Paths.get(dirPathString);
            Path filePath = dirPath.resolve(fileName);
            if (!this.serializedItem.containsKey(fileName)) {
                throw new IllegalArgumentException("File does not exist.");
            }
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method takes the fileName and the class of file, for example, commit and convert it to the actual
     * content.
     * @param className
     * @param fileName
     * @return
     * @throws IOException
     */

    public Object load(String className, String fileName) throws IOException {
        Path filePath = this.workingDir.toPath().resolve(fileName);
        InputStream fileInput = Files.newInputStream(filePath);
        ObjectInputStream objectInput = new ObjectInputStream(fileInput);
        Object get;
        XMLDecoder decodeFile = new XMLDecoder(fileInput);
        get = decodeFile.readObject();
        decodeFile.close();
        fileInput.close();
        objectInput.close();
        this.fileCache.put(filePath.toString(),get);
        return get;
    }

    /**
     * This method returns workingDir.
     * @return
     */
    public File getWorkingDir() {
        return this.workingDir;
    }
}
