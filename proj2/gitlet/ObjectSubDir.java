package gitlet;

public class ObjectSubDir extends OperationInDir {

    private static final int seperate = 2;
    enum ObjectItem {
        COMMIT(""), TREE(""); // those two types of obj and blob are all in objects subfolder.
        protected String workingDir;
        ObjectItem(String workingDir){
            this.workingDir = workingDir;
        }
    }



    /** construct a object subdirectory in the gitlet repo
     * @param file the directory user types in
     */
    public ObjectSubDir(String file) {
        super(file);

    }

    @Override
    public boolean contains(String filePath) {
        return super.contains(getShaValueFromPath(filePath));
    }
    @Override
    public boolean contains(String className, String filePath) {
        return super.contains(className, getShaValueFromPath(filePath));
    }
    @Override
    public void remove(String objType, String filePath) {
        super.remove(objType, getShaValueFromPath(filePath));
    }

    /**
     * This method push an gitVersion Object to the ObjectSubDir
     * @param obj
     * @return
     */
    public String push(ObjectInDir obj) {
        String sha = sha1(obj);
        if (!this.contains(sha)) {
            this.add((makeSubDir(sha)), sha);
        }
        return sha;
    }


    private static String makeSubDir(String sha) {
        return sha.substring(0, seperate) + "/" + sha.substring(seperate);
    }

    private static String getShaValueFromPath(String filePath) {
        return filePath.replace("/", "");
    }
}
