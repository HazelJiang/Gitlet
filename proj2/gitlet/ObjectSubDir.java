package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;


public class ObjectSubDir<T> extends OperationInDir<T> {

    private static final int seperate = 2;
    enum ObjectItem {
        COMMIT(""), TREE(""); // those two types of obj and blob are all in objects subfolder.
        protected String workingDir;
        ObjectItem(String workingDir){
            this.workingDir = workingDir;
        }
    }

    public class ObjectInDir implements Serializable {
        private static final long serialVersionUID = -1843757570228076096L;
        @Override
        public int hashCode() {
            return Utils.sha1(this).hashCode();
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            ObjectInDir other = (ObjectInDir) obj;

            return Utils.sha1(this).equals(Utils.sha1(other));
        }
    }


    /** construct a object subdirectory in the gitlet repo
     * @param file the directory user types in
     */
    public ObjectSubDir(String file) {
        super(file);

    }
    @Override
    public byte[] getFileContent(String filePath)throws IllegalArgumentException {
        return super.getFileContent(getShaValueFromPath(filePath));
    }
    @Override
    public byte[] getFileContent(String className, String filePath) throws IllegalArgumentException {
        return super.getFileContent(className, getShaValueFromPath(filePath));
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
    public void remove(String filePath) {
        super.remove(getShaValueFromPath(filePath));
    }

    /**
     * This method push an gitVersion Object to the ObjectSubDir
     * @param obj
     * @param <S>
     * @return
     */
    public <S extends T> String push(GitVersion obj) {
        String sha = obj.sha1();
        if (!this.contains(sha)) {
            this.add((S)makeSubDir(sha), sha);
        }
        return sha;
    }


    private static String makeSubDir(String sha) {
        return sha.substring(0, seperate) + "/" + sha.substring(seperate, sha.length());
    }

    private static String getShaValueFromPath(String filePath) {
        return filePath.replace("/", "");
    }
}
