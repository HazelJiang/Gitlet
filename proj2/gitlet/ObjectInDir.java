package gitlet;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
/*private void writeObject(java.io.ObjectOutputStream out)
     throws IOException
 private void readObject(java.io.ObjectInputStream in)
     throws IOException, ClassNotFoundException;
 private void readObjectNoData()
     throws ObjectStreamException; */

public abstract class ObjectInDir implements Serializable {
    private static final long serialVersionUID = -1843757570228076096L;
    public abstract String sha();

    public abstract String getClassName();
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ObjectInDir other = (ObjectInDir) obj;

        return this.sha().equals(other.sha());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public void writeToFile(String filename) throws java.io.IOException {
        ObjectOutputStream objectOutputStream =
                new ObjectOutputStream(new FileOutputStream("output.txt"));
        objectOutputStream.writeObject(this);
        objectOutputStream.close();
    }
}

