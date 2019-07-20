package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;


public class Repository {
    private static final String repo = "refs/";
    private static final String repoHead = "refs/head";
    private static final String objSub = "objects/";
    private static final String gitletRepo = ".gitlet";
    private File gitRepoDir;
    private File workingDir;
    private boolean isExist;
    /** This is a constructor method for making a repository
     * @param currentDir the directory user in
     */
    public Repository(String currentDir) {
        this.workingDir = new File(currentDir);
        String currentDirPath = workingDir.getAbsolutePath();
        Path workingPath = Paths.get(currentDirPath);
        Path gitletPath = workingPath.resolve(gitletRepo);
        Path objectPath = gitletPath.resolve(objSub);
        Path refPath = gitletPath.resolve(repo);
        Path refHeadPath = gitletPath.resolve(repoHead);
        try {
            if (!Files.exists(gitletPath)) {
                Files.createDirectory(gitletPath);
                Files.createDirectory(objectPath);
                Files.createDirectory(refPath);
                Files.createDirectory(refHeadPath);
            } else {
                isExist = true;
            }
        } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * This method initialize a new git repo if there doesn't exist a repo
     */
    public void init() {
        if (isExist) {
            throw new IllegalStateException(
                    "A gitlet version-control system already exists in the current directory.");
        } else {
            GitVersion newRepo = new GitVersion();
        }

    }

    public void status() {

    }
    /*log();
    global_log();
    find();
    removeBranch();*/

}
