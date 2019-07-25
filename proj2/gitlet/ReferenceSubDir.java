package gitlet;
import java.io.IOException;
import java.util.Set;

public class ReferenceSubDir extends OperationInDir {


    private ReferenceInDir.ReferenceType referenceType;
    private String objName;
    private ReferenceInDir refPointer;

    public ReferenceSubDir(String baseDir) {
        super(baseDir);
    }

    public void add(ReferenceInDir.ReferenceType referenceType, String objName, ReferenceInDir refPointer) {
        this.referenceType = referenceType;
        this.objName = objName;
        this.refPointer = refPointer;
        super.add(referenceType.toString() + objName, refPointer.getReferenceToSHAid());
    }

    public void add(ReferenceInDir.ReferenceType referenceType, ReferenceInDir refPointer) {
        this.add(referenceType, referenceType.toString(), refPointer);
    }

    public ReferenceInDir get(ReferenceInDir.ReferenceType refer, String fileName) throws IOException, ClassNotFoundException{
        ReferenceInDir ref = (ReferenceInDir) this.get(refer.toString(), refer.getBaseDir() + fileName);
        if (ref == null) {
            throw new IllegalArgumentException("no file exists");
        }
        return ref;
    }

    public ReferenceInDir get(ReferenceInDir.ReferenceType refer) throws IOException, ClassNotFoundException {
        return this.get(refer, refer.getBaseDir());
    }

    public boolean contains(ReferenceInDir.ReferenceType typeName, String fileName, String shaId) {
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
    public boolean contains(ReferenceInDir.ReferenceType typeName, String fileName){
        return this.contains(typeName, fileName, sha1(fileName));
    }

    public void remove(ReferenceInDir.ReferenceType typeName, String fileName) throws IllegalArgumentException {
        super.remove(typeName, this.getWorkingDir() + typeName.getBaseDir() + fileName);
    }

    public String getReferencePath(String baseDir) {
        String path = this.getWorkingDir().getAbsolutePath() + this.referenceType.toString();
        return path;
    }

    public ReferenceInDir.ReferenceType getReferenceType() {
        return this.referenceType;
    }
}
