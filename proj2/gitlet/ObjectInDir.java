package gitlet;

import java.io.Serializable;
/*private void writeObject(java.io.ObjectOutputStream out)
     throws IOException
 private void readObject(java.io.ObjectInputStream in)
     throws IOException, ClassNotFoundException;
 private void readObjectNoData()
     throws ObjectStreamException; */

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