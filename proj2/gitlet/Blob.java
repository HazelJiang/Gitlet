package gitlet;

public class Blob extends ObjectInDir {
    private static final long serialVersionUID = -3147881260082538484L;
    private Byte[] content;

    public Blob(Byte[] content) {
        this.content = content;
    }

    public Byte[] getBlobContent() {
        return content;
    }


}
