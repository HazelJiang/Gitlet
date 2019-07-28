package gitlet;
import java.io.*;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Scanner;



public class Repository extends OperationInDir {
    private static final String REPO = "refs";
    private static final String OBJSUB = "objects";
    private static final String GITLETREPO = ".gitlet";
    private static final String INDEXFILENAME = "index";
    // Class to deal with issues in the ".gitlet/refs" directory
    private ReferenceDir referenceDir;
    // Class to deal with issues in the ".gitlet/objects" directory
    private ObjectSubDir objectDir;
    // the ".gitlet" directory
    private File gitDir;
    // whether this repository is initialized
    private boolean initialized;
    // Current head branch in the directory
    private String branch;
    // The Index File
    private Index index;

    // Current commit of the repository.
    private Commit currentCommit;

    public Index getIndex() {
        return index;
    }

    public static Date getCurrentTime() {
        return new Date();
    }

    public ObjectSubDir getObjectDir() {
        return objectDir;
    }

    public Commit getCurrentCommit() {
        return currentCommit;
    }

    public void setCurrentCommit(Commit currentCommit) {
        this.currentCommit = currentCommit;
    }
    /** This is a constructor method for making a repository
     * @param currentDir the directory user in
     */
    public Repository(String currentDir) throws IOException, ClassNotFoundException {
        super(Paths.get(currentDir).toString());
        String currentDirPath = this.getWorkingDir().getAbsolutePath();
        this.gitDir = Paths.get(currentDirPath).resolve(GITLETREPO).toFile();
        if (gitDir.exists()) {
            initialized = true;
            // ".gitlet/refs"
            String referenceDirPath = Paths.get(gitDir.getAbsolutePath()).resolve(REPO).toString();
            this.referenceDir = new ReferenceDir(referenceDirPath);
            //".gitlet/objects"
            String objectDirPath = Paths.get(gitDir.getAbsolutePath()).resolve(OBJSUB).toString();
            this.objectDir = new ObjectSubDir(objectDirPath);
            File headFile = Utils.join(gitDir.getAbsolutePath(), "HEAD");
            Scanner headFileScanner = new Scanner(headFile);
            branch = headFileScanner.next();
            // Get the head commit
            currentCommit = (Commit) objectDir.get(referenceDir.getHead(branch));
            File indexFile = Utils.join(gitDir, "index");
            FileInputStream fis = new FileInputStream(indexFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            index = (Index)ois.readObject();
        }
    }

    public Index loadedIndex() throws IOException, ClassNotFoundException {
        return (Index) this.get("Index", "INDEX");
    }

    /**
     * This method initialize a new git repo if there doesn't exist a repo
     */
    public void init() throws IllegalStateException, IOException, ClassNotFoundException  {
        if (initialized) {
            throw new IllegalStateException(
                    "A gitlet version-control system already exists in the current directory.");
        } else {
            String initialCommit = "initial commit";
            // create directories
            gitDir.mkdirs();
            File refDir = Utils.join(gitDir.getAbsolutePath(), REPO);
            refDir.mkdirs();
            File objDir = Utils.join(gitDir.getAbsolutePath(), OBJSUB);
            objDir.mkdirs();
            // Create Objects for these directories
            objectDir = new ObjectSubDir(objDir.getAbsolutePath());
            referenceDir = new ReferenceDir(refDir.getAbsolutePath());
            // The commit object for our first commit
            currentCommit = new Commit(initialCommit, getCurrentTime());
            // Add the initial commit to the objects
            objectDir.add(currentCommit);
            // Create the master branch
            branch = "master";
            referenceDir.addBranch(branch, currentCommit.sha());
            writeHead();
            //Initialize the Index File
            index = new Index();
            writeIndex();
            initialized = true;
        }
    }

    /**
     * Adds a file to the Index area
     * @param file the file to add
     * @throws FileNotFoundException
     */
    public void addFile(File file) throws IOException {
        String fileName = file.getName();
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
        } catch(FileNotFoundException e) {
            throw new FileNotFoundException("File does not exist.");
        }
        byte[] fileContent = fis.readAllBytes();
        Blob fileBlob = new Blob(fileContent);
        /**
         * If the File is Indentical to the file in the current commit,
         * don't do anything.
         */
        if (currentCommit.containsFile(fileName)) {
            String oldFileSHA = currentCommit.get(fileName);
            if (oldFileSHA.equals(fileBlob.sha())) {
                if (index.getAddStage().containsKey(fileName)) {
                    index.getAddStage().remove(fileName);
                }
                return;
            }
        }
        objectDir.add(fileBlob);
        index.add(fileName, fileBlob.sha());
        writeIndex();
    }

    /**
     * Commit the changes currently in the index area.
     * @param message the commit message
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void commit(String message) throws Exception {
        if (index.getRemoveStage().isEmpty() && index.getAddStage().isEmpty()) {
            throw new Exception("No changes added to the commit.");
        }
        Commit commit = new Commit(message, getCurrentTime(), currentCommit.sha(), this);
        commit.mergeIndex(index);
        index = new Index();
        writeIndex();
        objectDir.add(commit);
        referenceDir.setHead(branch, commit.sha());
    }

    /**
     * Untrack the file; that is, indicate (somewhere in the .gitlet directory)
     * That it is not to be included in the.
     * next commit, even if it is tracked in the current commit.
     * (the current commit will eventually become the next.
     * commit’s parent). This command breaks down as follows:
     * If the file is tracked by the current commit,
     * delete the file from the working directory, unstage it if.
     * it was staged, and mark the file to be untracked by the next commit.
     * If the file isn’t tracked by the current commit but it is staged,
     * unstage the file and do nothing else.
     * (don’t remove the file!).
     * @param fileName the file to be removed
     */
    public void remove(String fileName) throws Exception {
        if (currentCommit.containsFile(fileName)) {
            // Delete the file from the working directory
            File removeFile = Utils.join(getWorkingDir(), fileName);
            if (!Utils.restrictedDelete(removeFile)) {
                throw new Exception("No reason to remove the file");
            }
            //Unstage it if it was staged and mark the file to be untracked by the next commit
            index.remove(fileName);
            writeIndex();
        } else if (index.staged(fileName)) {
            index.remove(fileName);
            writeIndex();
        } else {
            throw new Exception("No reason to remove the file");
        }
    }

    /**
     * Prints out the information of all the commits in the current branch.
     */
    public void log() throws IOException, ClassNotFoundException {
        Commit c = currentCommit;
        while (c != null) {
            System.out.println(c);
            System.out.println();
            System.out.println();
            c = c.getParentCommit(this);
        }
    }

    /**
     * Prints out all the information of all commits
     */
    public void globalLog() throws IOException, ClassNotFoundException {
        Set<String> loggedCommits = new HashSet<>();
        for(String sha: referenceDir.getHeads().values()) {
            Commit c = (Commit) objectDir.get(sha);
            while (c != null) {
                if (loggedCommits.contains(c.sha())) {
                    break;
                }
                System.out.println(c);
                System.out.println();
                loggedCommits.add(c.sha());
                c = c.getParentCommit(this);
            }
        }
    }

    /**
     * Displays what branches currently exist, and marks the current branch with a *.
     * Also displays what files have.
     * been staged or marked for untracking.
     * An example of the exact format it should follow is as follows.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void status() throws IOException, ClassNotFoundException {
        System.out.println("=== Branches ===");
        for (String branchEnumeration: referenceDir.getHeads().keySet()) {
            if (branch.equals(branchEnumeration)) {
                System.out.println("*" + branchEnumeration);
            } else {
                System.out.println(branchEnumeration);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        for (String addedFileName: index.getAddStage().keySet()) {
            System.out.println(addedFileName);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String removedFileName: index.getRemoveStage()) {
            System.out.println(removedFileName);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        Commit tempCommit = new Commit("", null, currentCommit.sha(), this);
        tempCommit.mergeIndex(index);
        Set<String> removedFiles = new HashSet<>();
        File working = this.getWorkingDir();
        File[] currentFiles = working.listFiles();
        Set<String> currentFilesSet = new HashSet<String>();
        for (File f: currentFiles) {
            if (!f.isDirectory()) {
                currentFilesSet.add(f.getName());
            }
        }
        Set<String> trackedFiles = tempCommit.getBlob().keySet();
        Set<String> untrackedFiles = new HashSet<>();
        /**
         * Untracked files:
         * files that are in the current working directory but not in the stage or commit.
         */
        for (String s: currentFilesSet) {
            if (!trackedFiles.contains(s)) {
                untrackedFiles.add(s);
            }
        }
        Set<String> deletedFiles = new HashSet<>();
        Set<String> bothExist = new HashSet<>();
        /**
         * Deleted files:
         * files that are tracked or commited but not found in the current directory.
         */
        for (String s: trackedFiles) {
            if (!currentFilesSet.contains(s)) {
                deletedFiles.add(s);
            } else {
                bothExist.add(s);
            }
        }
        /**
         * Modified files:
         * files that exists in both the working directory and the new commit but different.
         */
        Set<String> modifiedFiles = new HashSet<>();
        for (String fileName: bothExist) {
            String sha1 = tempCommit.get(fileName);
            File f = Utils.join(getWorkingDir(), fileName);
            FileInputStream fis = new FileInputStream(f);
            Blob tempBlob = new Blob(fis.readAllBytes());
            fis.close();
            String sha2 = tempBlob.sha();
            if (!sha1.equals(sha2)) {
                modifiedFiles.add(fileName);
            }
        }
        for (String deletedFile: deletedFiles) {
            System.out.println(deletedFile + " (deleted)");
        }
        for (String modifiedFile: modifiedFiles) {
            System.out.println(modifiedFile + " (modified)");
        }
        System.out.println();
        System.out.println("=== Untracked Files ===");
        for (String untrackedFile: untrackedFiles) {
            System.out.println(untrackedFile);
        }
        System.out.println();
    }

    /**
     * Restore the content of a file with the content it stored in the given commit.
     * @param fileName the file to restore
     * @param commitSHA the SHA ID of the commit to restore the file from
     * @throws Exception
     */
    public void checkoutFileWithCommit(String fileName, String commitSHA) throws Exception {
        if (!objectDir.contains(commitSHA)) {
            throw new Exception("No commit with that id exists.");
        }
        Commit desiredCommit = (Commit) objectDir.get(commitSHA);
        if (!desiredCommit.containsFile(fileName)) {
            throw new Exception("File does not exist in that commit.");
        }
        String fileSHA = desiredCommit.get(fileName);
        Blob fileBlob = (Blob) objectDir.get(fileSHA);
        File desiredFile = Utils.join(getWorkingDir(), fileName);
        FileOutputStream fos = new FileOutputStream(desiredFile);
        fos.write(fileBlob.getBlobContent());
    }

    /**
     * Restore the contents of a file with the its content in the current commit.
     * @param fileName the file to restore
     * @throws Exception
     */
    public void checkoutFileWithCurrentCommit(String fileName) throws Exception {
        checkoutFileWithCommit(fileName, currentCommit.sha());
    }

    /**
     * Restore all files in the head commit of a branch
     * @param branchName the name of the branch to restore.
     * @throws Exception
     */
    public void checkoutBranch(String branchName) throws Exception {
        if (!referenceDir.containsBranch(branchName)) {
            throw new Exception("No such branch exists.");
        }
        if (branchName.equals(branch)) {
            throw new Exception("No need to check out the current branch.");
        }
        String desiredCommit = referenceDir.getHead(branchName);
        reset(desiredCommit, false);
        branch = branchName;
        writeHead();
        currentCommit = (Commit) objectDir.get(desiredCommit);
    }

    /**
     * Create a new branch that initially points at the same commit as this one.
     * @param branchName the name of the new branch.
     * @throws Exception
     */
    public void branch(String branchName) throws Exception {
        if (referenceDir.containsBranch(branchName)) {
            throw new Exception("A branch with that name already exists.");
        }
        referenceDir.addBranch(branchName, currentCommit.sha());
    }

    /**
     * Removes a branch with the given name. Does not do delete any commit.
     * @param branchName
     * @throws Exception
     */
    public void rmBranch(String branchName) throws Exception {
        if (!referenceDir.containsBranch(branchName)) {
            throw new Exception("A branch with that name does not exist.");
        }
        if (branchName.equals(branch)) {
            throw new Exception("Cannot remove the current branch.");
        }
        referenceDir.removeBranch(branchName);
    }

    /**
     * Resets all the files in the working directory to a previous commit.
     * @param commitID the id of the commit to restore
     * @param setPointer argument for reusability by "checkout"
     * @throws Exception
     */
    public void reset(String commitID, boolean setPointer) throws Exception {
        if (!objectDir.contains(commitID)) {
            throw new Exception("No commit with that id exists.");
        }
        /**
         * See whether there are untracked files
         */
        Commit tempCommit = new Commit("", null, currentCommit.sha(), this);
        tempCommit.mergeIndex(index);
        File working = this.getWorkingDir();
        File[] currentFiles = working.listFiles();
        Set<String> currentFilesSet = new HashSet<String>();
        for (File f: currentFiles) {
            if (!f.isDirectory()) {
                currentFilesSet.add(f.getName());
            }
        }
        Set<String> trackedFiles = tempCommit.getBlob().keySet();
        /**
         * Untracked files:
         * files that are in the current working directory but not in the stage or commit.
         */
        for (String s: currentFilesSet) {
            if (!trackedFiles.contains(s)) {
                throw new Exception("There is an untracked file in the way; delete it or add it first.");
            }
        }
        /**
         * Start by deleting the whole working directory except for ".gitlet"
         */
        File workingDir = getWorkingDir();
        for (File f: workingDir.listFiles()) {
            if (f.isDirectory()) {
                continue;
            }
            f.delete();
        }

        /**
         * Then we put all the files in the commit into the working dir
         */
        Commit desiredCommit = (Commit) objectDir.get(commitID);
        for (String fileName: desiredCommit.getBlob().keySet()) {
            File f = Utils.join(getWorkingDir(), fileName);
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(((Blob) objectDir.get(desiredCommit.get(fileName))).getBlobContent());
            fos.close();
        }

        if (setPointer) {
            referenceDir.setHead(branch, commitID);
        }

        currentCommit = desiredCommit;
    }

    public void merge(String branchName) throws Exception {
        if (!index.getAddStage().isEmpty() || !index.getRemoveStage().isEmpty()) {
            throw new Exception("You have uncommitted changes.");
        }
        if (!referenceDir.containsBranch(branchName)) {
            throw new Exception("A branch with that name does not exist.");
        }
        if (branch.equals(branchName)) {
            throw new Exception("Cannot merge a branch with itself.");
        }
        Commit tempCommit = new Commit("", null, currentCommit.sha(), this);
        tempCommit.mergeIndex(index);
        File working = this.getWorkingDir();
        File[] currentFiles = working.listFiles();
        Set<String> currentFilesSet = new HashSet<String>();
        for (File f: currentFiles) {
            if (!f.isDirectory()) {
                currentFilesSet.add(f.getName());
            }
        }
        Set<String> trackedFiles = tempCommit.getBlob().keySet();
        /**
         * Untracked files: files that are in the current working directory but not in the stage or commit.
         */
        Set<String> untrackedFiles = new HashSet<>();
        for (String s: currentFilesSet) {
            if (!trackedFiles.contains(s)) {
                throw new Exception("There is an untracked file in the way; delete it or add it first.");
            }
        }
        Set<String> allParentCommits = new HashSet<>();
        Commit c = currentCommit;
        while (c != null) {
            allParentCommits.add(c.sha());
            c = c.getParentCommit(this);
        }
        Commit otherBranchCommit = c = (Commit) objectDir.get(referenceDir.getHead(branchName));
        while (c != null) {
            if (allParentCommits.contains(c.sha())) {
                break;
            }

            if (c.getParentCommit(this) == null) {
                break;
            }
            c = c.getParentCommit(this);
        }
        if (c.sha().equals(currentCommit.sha())) {
            reset(otherBranchCommit.sha(), false);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        if (c.sha().equals(otherBranchCommit.sha())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        Set<String> modifiedFilesInCurrentCommit = new HashSet<>();
        Set<String> modifiedFilesInOtherCommit = new HashSet<>();
        for (String fileName: c.getBlob().keySet()) {
            if (!currentCommit.containsFile(fileName)) {
                modifiedFilesInCurrentCommit.add(fileName);
            }
            if (!otherBranchCommit.containsFile(fileName)) {
                modifiedFilesInOtherCommit.add(fileName);
            }
            if (currentCommit.containsFile(fileName) && !c.get(fileName).equals(currentCommit.get(fileName))) {
                modifiedFilesInCurrentCommit.add(fileName);
            }
            if (otherBranchCommit.containsFile(fileName) && !c.get(fileName).equals(otherBranchCommit.get(fileName))) {
                modifiedFilesInOtherCommit.add(fileName);
            }
        }
        for (String fileName: currentCommit.getBlob().keySet()) {
            if (!c.containsFile(fileName)) {
                modifiedFilesInCurrentCommit.add(fileName);
            }
        }
        for (String fileName: otherBranchCommit.getBlob().keySet()) {
            if (!c.containsFile(fileName)) {
                modifiedFilesInOtherCommit.add(fileName);
            }
        }
        boolean conflict = false;
        for (String fileName: modifiedFilesInCurrentCommit) {
            if (!modifiedFilesInOtherCommit.contains(fileName)) {
                // Modified in this commit but not in the other
                continue;
            }
            else {
                if (!currentCommit.containsFile(fileName) && !otherBranchCommit.containsFile(fileName)) {
                    continue;
                }
                if (!currentCommit.containsFile(fileName)) {
                    conflict = true;
                    conflictOutput(fileName, currentCommit, otherBranchCommit);
                }
                if (!currentCommit.get(fileName).equals(otherBranchCommit.get(fileName))) {
                    conflict = true;
                    conflictOutput(fileName, currentCommit, otherBranchCommit);
                }
            }
        }
        for (String fileName: modifiedFilesInOtherCommit) {
            if (!modifiedFilesInCurrentCommit.contains(fileName)) {
                File f = Utils.join(getWorkingDir(), fileName);
                if (!otherBranchCommit.containsFile(fileName)) {
                    remove(fileName);
                } else {
                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(((Blob) objectDir.get(otherBranchCommit.get(fileName))).getBlobContent());
                    addFile(f);
                }
            }
        }
        if (!conflict) {
            commit("Merged " + branch + " with " + branchName + ".");
        } else {
            System.out.println("Encountered a merge conflict.");
        }
    }

    private void conflictOutput(String fileName, Commit currentCommit, Commit otherCommit) throws IOException, ClassNotFoundException {
        File f = Utils.join(getWorkingDir(), fileName);
        if (!f.exists()) {
            f.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(f);
        fos.write("<<<<<<< HEAD\n".getBytes());
        if (currentCommit.containsFile(fileName)) {
            fos.write(((Blob)(objectDir.get(currentCommit.get(fileName)))).getBlobContent());
        }
        fos.write("=======\n".getBytes());
        if (otherCommit.containsFile(fileName)) {
            fos.write(((Blob)(objectDir.get(otherCommit.get(fileName)))).getBlobContent());
        }
        fos.write(">>>>>>>".getBytes());
    }

    private void writeHead() throws FileNotFoundException, IOException {
        File headFile = Utils.join(gitDir.getAbsolutePath(), "HEAD");
        if (!headFile.exists()) {
            headFile.createNewFile();
        }
        FileOutputStream o = new FileOutputStream(headFile);
        o.write(branch.getBytes());
    }

    /**
     * Write the index object to a file
     * @throws IOException
     */
    private void writeIndex() throws IOException {
        File indexFile = Utils.join(gitDir, INDEXFILENAME);
        if (!indexFile.exists()) {
            indexFile.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(indexFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(index);
    }

    /**
     * Read the index object from a file
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readIndex() throws IOException, ClassNotFoundException {
        File indexFile = Utils.join(gitDir, INDEXFILENAME);
        if (!indexFile.exists()) {
            throw new FileNotFoundException("The index file does not exist!");
        }
        FileInputStream fis = new FileInputStream(indexFile);
        ObjectInputStream ois = new ObjectInputStream(fis);
        index = (Index)ois.readObject();
    }
}
