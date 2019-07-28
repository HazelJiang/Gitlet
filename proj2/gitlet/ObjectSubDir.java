package gitlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * A class that manages the Object subdirectory.
 * Main function is to store and get serializable objects with SHA codes.
 */
public class ObjectSubDir {
    public static final int SHACUT = 2;
    // The working directory of the object subdirectory
    private File objDir;
    // All contained SHA codes in the directory
    private Set<String> shaCodes;

    public ObjectSubDir() { }

    /**
     * Constructor that takes the object directory
     * as parameter and reads all available SHA Codes from the directory
     * @param objDir the working object directory
     */
    public ObjectSubDir(String objDir) {
        this(new File(objDir));
    }

    /**
     * Constructor that takes the object directory as
     * parameter and reads all available SHA Codes from the directory
     * @param objDir the working object diretory.
     */
    public ObjectSubDir(File objDir) {
        this.objDir = objDir;
        shaCodes = new HashSet<String>();
        readAllSHA();
    }

    /**
     * Add an object in the directory using SHA code
     * @param object the serializable and SHA object to add
     */
    public void add(gitlet.ObjectInDir object) throws IOException {
        String objectSHA = object.sha();
        if (shaCodes.contains(objectSHA)) {
            //throw new RuntimeException("There's either a SHA collision
            // or you're adding a existing item.");
            return;
        }
        String shaHead = objectSHA.substring(0, 2);
        String shaTail = objectSHA.substring(2);
        File shaFileFolder = gitlet.Utils.join(objDir, shaHead);
        if (!shaFileFolder.exists()) {
            shaFileFolder.mkdirs();
        }
        File shaFile = gitlet.Utils.join(objDir, shaHead, shaTail);
        shaFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(shaFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
    }

    /**
     * Get an object from SHA code
     * @param objectSHA the SHA code of the object to get
     */
    public ObjectInDir get(String objectSHA) throws IOException, ClassNotFoundException {
        if (!contains(objectSHA)) {
            return null;
        }
        String shaHead = objectSHA.substring(0, 2);
        String shaTail = objectSHA.substring(2);
        File shaFile = Utils.join(objDir, shaHead, shaTail);
        FileInputStream fis = new FileInputStream(shaFile);
        ObjectInputStream ois = new ObjectInputStream(fis);
        return (ObjectInDir) ois.readObject();
    }

    /**
     * Whether an object with the specific SHA code already exist.
     * @param objectSHA the SHA code of the object
     * @return Whether the object already exist.
     */
    public boolean contains(String objectSHA) {
        return shaCodes.contains(objectSHA);
    }

    /**
     * Get all the available SHA codes stored in the folder
     */
    private void readAllSHA() {
        File[] files = objDir.listFiles();
        for (File shaHeadFolder: files) {
            if (shaHeadFolder.isDirectory()) {
                String shaHead = shaHeadFolder.getName();
                if (shaHead.length() != SHACUT) {
                    throw new RuntimeException("There is a folder "
                            + "that does not satisfy the sha requirement!");
                }
                for (File shaFile: shaHeadFolder.listFiles()) {
                    shaCodes.add(shaHead + shaFile.getName());
                }
            }
        }
    }
}
