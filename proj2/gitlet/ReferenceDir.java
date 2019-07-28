package gitlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Class to deal with operations in the "refs" directory.
 * Main function is setting heads of branches.
 * We would simply store the heads in the "refs" directory rather
 * than the "refs/heads" for simplicity because there
 * are no other functions in gitlet.
 */
public class ReferenceDir {
    File refDir;
    // Key: branch name. Value: sha code of the branch's last commit
    Map<String, String> heads;

    /**
     * Constructor to initialize the class with the "refs" directory.
     * Reads the current existing branch names and their heads.
     * @param refDir the "refs" directory
     */
    public ReferenceDir(File refDir) throws FileNotFoundException {
        this.refDir = refDir;
        heads = new HashMap<>();
        readHeads();
    }

    /**
     * Constructor to initialize the class with the "refs" directory.
     * Reads the current existing branch names and their heads.
     * @param refDir the "refs" directory
     */
    public ReferenceDir(String refDir) throws FileNotFoundException {
        this(new File(refDir));
    }

    public Map<String, String> getHeads() {
        return heads;
    }

    /**
     * Read the names of the existing heads in the "refs" directory
     * and their corresponding head commit SHA code.
     */
    private void readHeads() throws FileNotFoundException {
        for (File head: refDir.listFiles()) {
            Scanner headSHAScanner = new Scanner(head);
            String SHA = headSHAScanner.nextLine();
            heads.put(head.getName(), SHA);
            headSHAScanner.close();
        }
    }

    /**
     * Get the sha value of the head's last commit
     * @param branchName the name of the branch
     * @return the branch's last commit's SHA value
     */
    public String getHead(String branchName) {
        return heads.get(branchName);
    }

    /**
     * Adds a new branch and set its head commit
     * @param branchName name of the new branch
     * @param head SHA value of the head commit of the newly created branch
     * @throws Exception When there's some error with File IO
     */
    public void addBranch(String branchName, String head) throws IOException {
        if (containsBranch(branchName)) {
            throw new RuntimeException("Adding an existing branch");
        }
        heads.put(branchName, head);
        File newHead = Utils.join(refDir, branchName);
        newHead.createNewFile();
        FileOutputStream fos = new FileOutputStream(newHead);
        fos.write(head.getBytes());
        fos.close();
    }

    /**
     * Returns whether a branch of a given name already exist.
     * @param branchName the name of the branch to be checked
     * @return Whether the branch exist
     */
    public boolean containsBranch(String branchName) {
        return heads.containsKey(branchName);
    }

    /**
     * Removes a branch
     * @param branchName the name of the branch to be removed
     */
    public void removeBranch(String branchName) {
        if (!containsBranch(branchName)) {
            throw new RuntimeException("Removing a branch that does not exist");
        }
        heads.remove(branchName);
        File removingHead = Utils.join(refDir, branchName);
        removingHead.delete();
    }

    public void setHead(String branch, String headCommit) throws IOException {
        if (!heads.containsKey(branch)) {
            throw new RuntimeException("Setting " +
                    "the head of a non-existent branch.");
        }
        heads.put(branch, headCommit);
        File newHead = Utils.join(refDir, branch);
        FileOutputStream fos = new FileOutputStream(newHead);
        fos.write(headCommit.getBytes());
        fos.close();
    }
}
