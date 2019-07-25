package gitlet;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import static gitlet.ReferenceInDir.ReferenceType.BRANCH;
import static gitlet.ReferenceInDir.ReferenceType.HEAD;


public class Repository extends OperationInDir {
    private static final String repo = "refs/";
    private static final String objSub = "objects/";
    private static final String gitletRepo = ".gitlet";
    private static final String index = "index";
    private ReferenceSubDir referenceDir;
    private ObjectSubDir objectDir;
    private File gitDir;
    private File workingDir;
    private boolean isExist;

    /** This is a constructor method for making a repository
     * @param currentDir the directory user in
     */
    public Repository(String currentDir) {
        super(Paths.get(currentDir).toString());
        String currentDirPath = this.getWorkingDir().getAbsolutePath();
        this.gitDir = Paths.get(currentDirPath).resolve(gitletRepo).toFile();
        this.workingDir = Paths.get(currentDirPath).toFile();
        String referenceDirPath = Paths.get(gitDir.getAbsolutePath()).resolve(repo).toString();
        this.referenceDir = new ReferenceSubDir(referenceDirPath);
        String objectDirPath = Paths.get(gitDir.getAbsolutePath()).resolve(objSub).toString();
        this.objectDir = new ObjectSubDir(objectDirPath);
        try {
            if (!gitDir.exists()) {
                new File(referenceDirPath).mkdir();
                new File(objectDirPath).mkdir();
            } else {
                isExist = true;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Index loadedIndex() throws IOException, ClassNotFoundException {
        return (Index) this.get("Index", "INDEX");
    }

    /**
     * This method initialize a new git repo if there doesn't exist a repo
     */
    public void init() {
        if (isExist) {
            throw new IllegalStateException(
                    "A gitlet version-control system already exists in the current directory.");
        } else {
            String initalCommit = "initial commit";
            LocalDateTime localDateTime = LocalDateTime.now();
            ZoneId zone = ZoneId.systemDefault();
            Instant instant = localDateTime.atZone(zone).toInstant();
            Date date = Date.from(instant);
            this.getObjectDir().push(new Commit(initalCommit, date));
            this.getReferenceDir().add(ReferenceInDir.ReferenceType.BRANCH, "master", new ReferenceInDir(initalCommit));
            this.getReferenceDir().add(HEAD, new ReferenceInDir(ReferenceInDir.ReferenceType.BRANCH, "master"));
            this.add(new Index(), sha1(index));
        }
    }
    public void checkout (Commit commit, String fileName, boolean onStage) throws IOException, ClassNotFoundException {
        String commitGet = commit.get(fileName);
        if (commitGet == null) {
            throw new IllegalArgumentException("File does not exist in that commit.");
        }
        Blob blob = (Blob) this.getObjectDir().get("Blob", commitGet);
        File fileRewrite = this.workingDir.toPath().resolve(fileName).toFile();
        String fileContent = null;
        Scanner fileScan = new Scanner(fileRewrite);
        while (fileScan.hasNext()) {
            fileContent += fileScan.next();
        }
        PrintStream outFile = new PrintStream(new File(fileRewrite.getPath()));
        outFile.println(fileContent);
        Index index = this.loadedIndex();
        index.checkout(fileName, commitGet, onStage);
    }

    public void checkout (Commit commit) throws IOException, ClassNotFoundException {
        Index index = this.loadedIndex();
        File[] fileList = this.getWorkingDir().listFiles();
        for (File file : fileList) {
            if (file.isFile()) {
                String fileName = file.getName();
                if (commit.containKeys(fileName) && !index.getBlob().containsKey(fileName)) {
                    throw new IOException("There is an untracked file in the way; delete it or add it first.");
                } else if (commit.containKeys(fileName) && !index.getAddStaged().containsKey(fileName)) {
                    throw new IOException("There is an untracked file in the way; delete it or add it first.");
                }
                if (index.getBlob().containsKey(fileName)) {
                    Files.delete(file.toPath());
                    return;
                }
            } else {
                File[] filelist = file.listFiles();
                if (filelist == null) {
                    System.out.println("No such file or directory.\n");
                    return;
                }
                for (File subfile : filelist) {
                    checkout(commit);
                }
            }
        }
    }
    public ReferenceInDir getCurrentBranch() throws IOException, ClassNotFoundException{
        ReferenceInDir head = this.getReferenceDir().get(HEAD);
        return this.getReferenceDir().get(BRANCH, head.getReferenceToSHAid());

    }

    public String addCommitToCurrentHead(String message, HashMap<String, String> blobs) throws IOException, ClassNotFoundException{
        Path headPath = Paths.get(this.getReferenceDir().getReferencePath(this.getWorkingDir().getAbsolutePath())).resolve(this.getReferenceDir().getReferenceType().toString());
        LocalDateTime localDateTime = LocalDateTime.now();
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        Date date = Date.from(instant);
        String commitSHA = this.getObjectDir().push(new Commit(message, date, headPath.toString(), blobs));
        this.getCurrentBranch().setPointer(commitSHA);
        return commitSHA;
    }


   public void setCurrentBranch(String branchName) throws IOException, ClassNotFoundException {
        this.getReferenceDir().get(BRANCH, branchName);
        this.getReferenceDir().get(HEAD).setPointer(branchName);
   }
    public ObjectSubDir getObjectDir() {
        return this.objectDir;
    }

    public ReferenceSubDir getReferenceDir() {
        return this.referenceDir;
    }

    public File getWorkingDir() {
        return this.workingDir;
    }
}
