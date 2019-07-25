package gitlet;

import java.io.Serializable;

public class ReferenceInDir implements Serializable {
    private static final long serialVersionUID = -7463964180111243727L;
    private String referenceToSHAid;
    enum ReferenceType {
        HEAD(""), BRANCH("branches/"), TAG("tags/"), NOSPECIFY("");
        private String baseDir;
        ReferenceType(String s) {
            this.baseDir = s;
        }
        public String getBaseDir() {
            return this.baseDir;
        }
        public String toString() {
            return this.baseDir;
        }
    }

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
        this(ReferenceType.NOSPECIFY,Utils.sha1(referenceObjectName));
    }

    /** get the shaID, or say the object this reference refer to
     * @return the shaID of this object.
     */

    public String getReferenceToSHAid() {
        return this.referenceToSHAid;
    }

    public void setPointer(String targetSHAid) {
        this.referenceToSHAid = targetSHAid;
    }

    /**get this reference Type, if a head, branch, or tag
     * @return
     */
    public ReferenceType getReferenceType() {
        return this.referenceType;
    }

}
