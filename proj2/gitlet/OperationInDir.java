/** This class represents how to hash a file, the context of a file, and the name of a file in gitlet
 * The methods below are shared by Object, referenceSubDir.
 */
package gitlet;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class OperationInDir extends Utils implements Serializable {
    /**
     * objects have been hashed, string is the type of the object, for example: blob
     * the strings in sets are shaID about the specific operations. For set, the actual dynamic
     * type is linkedHashSet, so we can trace the sequence to add commit, tree or branch etc.
     */
    protected HashMap<String, Set<String>> AddedInSep = new HashMap<>();
    /**
     * objects have been loaded in the cache, take the specific shaID to get the files have been loaded
     */
    protected HashMap<String, Object> ObjectCache = new HashMap<>();
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
    protected HashMap<String, String> serialItem = new HashMap<>();

//    public class Key {
//        private String className;
//        private String shaID;
//
//        public Key(String className, String shaID) {
//            this.className = className;
//            this.shaID = shaID;
//        }
//        @Override
//        public boolean equals(Object obj) {
//            if (this == obj) {
//                return true;
//            }
//            if (!(obj instanceof Key)) return false;
//            Key key = (Key) obj;
//            return (className == key.className && shaID == key.shaID);
//        }
//    }
    /**
     * This is a constructor that takes in the directory
     * we will use and creates a hashed object
     * @param Dir the directory we will hash or files in it we will hash.
     */
    public OperationInDir(String Dir) {
        this.workingDir = new File(Dir);
    }

    /** take the Object Item or Reference Item, and add the specific SHAId to the folder
     * @param objName, this is the object of the item we want to add in.
     */
    public void add(String objName , String hashId) {
        if (!AddedInSep.containsKey(objName)) {
            Set<String> trackedSHA = new LinkedHashSet<>();
            AddedInSep.put(objName, trackedSHA);
        } else {
           if (this.contains(hashId)) {
               throw new IllegalArgumentException("This object has already in the directory");
           } else {
               AddedInSep.get(objName).add(hashId);
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
     * @param obj
     * @param shaID
     * @return
     */
    public boolean contains(String obj, String shaID) {
        Set<String> SHA = this.AddedInSep.get(obj);
        if (SHA == null) {
            return false;
        }
        return SHA.contains(shaID);
    }

    public Object get(String objType, String objName) throws IOException, ClassNotFoundException{
        String objShaValue = this.serialItem.get(objType);
        Object objGet = this.ObjectCache.get(objShaValue);
        if (objGet == null) {
            return this.load(objType, objName);
        } else {
            return objGet;
        }
    }


//    /** This method takes the filepath that saved in serializeItem hashmap and get the serialized
//     * file content.
//     * @param filePath
//     * @return
//     * @throws IllegalArgumentException
//     */
//    public byte[] getFileContent(String filePath) throws IllegalArgumentException {
//        byte[] serialFile = this.serializedItem.get(filePath);
//        return serialFile;
//    }
//
//    /**
//     * This method takes a SHA id with specific type to check whether it exist and return this object.
//     * @param className
//     * @param SHAid
//     * @return
//     * @throws IllegalArgumentException
//     */
//    public byte[] getFileContent(String className, String SHAid) throws IllegalArgumentException {
//        if (AddedInSep.get(className) == null) {
//            throw new IllegalArgumentException("this object doesn't exist");
//        } else {
//            Set<String> SHA = this.AddedInSep.get(className);
//            Iterator<String> iterator = SHA.iterator();
//            while (iterator.hasNext()) {
//                if (iterator.next().equals(SHAid)){
//                    byte[] content = this.serializedItem.get(SHAid);
//                    return content;
//                }
//            }
//        }
//        return  null;
//    }

    /** This method remove a object by the objectName in the current directory
     * print "File does not exist." if file passed doesn't in the current Directory.
     * @param objName
     * @param  objValue
     */
    public void remove(String objName, String objValue) {
        try {
            String dirPathString = workingDir.getAbsolutePath();
            Path dirPath = Paths.get(dirPathString);
            Path filePath = dirPath.resolve(objName);
            if (!this.serialItem.containsValue(objName) || !this.ObjectCache.containsKey(objName)) {
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
     * @param objType
     * @param objectName
     * @return
     * @throws IOException
     */

   public Object load(String objType, String objectName) throws IOException, ClassNotFoundException{
       Path objectPath = Path.of(this.getWorkingDir().getAbsolutePath()).resolve(objectName);
       InputStream loadedOne = Files.newInputStream(objectPath);
       ObjectInputStream ObjectTransform = new ObjectInputStream(loadedOne);
       Object objGet;
       objGet = ObjectTransform.readObject();
       loadedOne.close();
       ObjectTransform.close();
       this.ObjectCache.put(objectName, objGet);
       return objGet;
   }

    /**
     * This method returns workingDir.
     * @return
     */
    public File getWorkingDir() {
        return this.workingDir;
    }
}
