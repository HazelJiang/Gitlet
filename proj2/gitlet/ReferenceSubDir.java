package gitlet;

import java.io.File;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.util.Set;

public class ReferenceSubDir<T> extends OperationInDir<T>{

    enum ReferenceType {
        HEAD(""), BRANCH("branches/"), TAG("tags/"), NOSPECIFY("");
        private String baseDir;
        ReferenceType(String s) {
            this.baseDir = s;
        }
        public String getBaseDir() {
            return this.baseDir;
        }
    }
    public ReferenceSubDir(String baseDir) {
        super(baseDir);
    }

    public class ReferenceInDir<T> implements Serializable{
        private static final long serialVersionUID = -7463964180111243727L;
        private String referenceToSHAid;
        private ReferenceType referenceType;
        public ReferenceInDir(ReferenceType referenceObjType, String referenceTo) {
            this.referenceType = referenceObjType;
            this.referenceToSHAid = referenceTo;
        }
        /**
         * creates a reference which targets to the referenceTo Object
         * @param referenceObjectName
         */
        public ReferenceInDir(String referenceObjectName) {
            this(ReferenceType.NOSPECIFY,sha1(referenceObjectName));
        }

        /** get the shaID, or say the object this reference refer to
         * @return the shaID of this object.
         */

        public String getReferenceToSHAid() {
            return this.referenceToSHAid;
        }

        /**get this reference Type, if a head, branch, or tag
         * @return
         */
        public ReferenceType getReferenceType() {
            return this.referenceType;
        }

    }
    public void add(ReferenceType referenceType, String fileName, ReferenceInDir refPointer) {
        super.add(referenceType.toString() + fileName, refPointer.getReferenceToSHAid());
    }

    public byte[] getFileContent(ReferenceType referenceType, String fileName)
            throws IllegalArgumentException {
        String fileSha = sha1(fileName);
        Set<String> shaValue = this.AddedInSep.get(referenceType.toString() + fileName);
        if (shaValue == null) {
            throw new IllegalArgumentException("this reference doesn't exist");
        }
        for (String eachSha: shaValue) {
            if (eachSha.equals(fileSha)) {
                return this.serializedItem.get(fileSha);
            }
        }
        return null;
    }
    public boolean contains(ReferenceType typeName, String fileName, String shaId) {
        Set<String> shaValue = this.AddedInSep.get(typeName.toString() + fileName);
        if (shaValue != null) {
            for (String sha : shaValue) {
                if (sha.equals(shaId)) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean contains(ReferenceType typeName, String fileName){
        return this.contains(typeName, fileName, sha1(fileName));
    }

    public void remove(ReferenceType typeName, String fileName) throws IllegalArgumentException {
        super.remove(this.getWorkingDir() + typeName.getBaseDir() + fileName);
    }
}
