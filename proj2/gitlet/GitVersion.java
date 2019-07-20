package gitlet;

import java.io.Serializable;
/*private void writeObject(java.io.ObjectOutputStream out)
     throws IOException
 private void readObject(java.io.ObjectInputStream in)
     throws IOException, ClassNotFoundException;
 private void readObjectNoData()
     throws ObjectStreamException; */

public class GitVersion extends Utils implements Serializable {

    //private final String refBranch = "refs/branchs";
    public GitVersion() {
        Commit instance = new Commit("initial commit");
        Branch newbranch = new Branch("master");
        newbranch.setPointer(instance);
    }

    public String CommitID() {
        return sha1(this);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        GitVersion other = (GitVersion) obj;
        return sha1(other).equals(sha1(this));
    }
}
