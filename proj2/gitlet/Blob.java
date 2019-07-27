package gitlet;

public class Blob extends ObjectInDir {
    private static final long serialVersionUID = -3147881260082538484L;
    private byte[] content;

    public Blob(byte[] content) {
        this.content = content;
    }

    public byte[] getBlobContent() {
        return content;
    }

    public String sha() {
        return Utils.sha1(content);
    }

    public String getClassName() {
        return "Blob";
    }
}
