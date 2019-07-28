package gitlet;

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.io.FileOutputStream;

/* The suite of all JUnit tests for the gitlet package.
   @author
 */
public class UnitTest {

    public void initialize() {
        System.out.println(System.getProperty("user.dir"));
        try {
            Command.InitCommand.callInit(new Repository("Test"), new String[0]);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void writeToFile(String s, String fileName) throws Exception {
        File f = Utils.join(System.getProperty("user.dir"), "Test", fileName);
        if (!f.exists()) {
            f.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(s.getBytes());
        fos.close();
    }

    public void addFile(String[] files) throws Exception {
        for (String f: files) {
            Command.AddCommand.callAdd(new Repository("Test"), new String[]{"add", f});
        }
    }

    @Test
    public void test_init() throws Exception {
        System.out.println(System.getProperty("user.dir"));
        try {
            Command.InitCommand.callInit(new Repository("Test"), new String[0]);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void test_add() throws Exception {
        Command.InitCommand.callInit(new Repository("Test"), new String[0]);
        Command.AddCommand.callAdd(new Repository("Test"), new String[]{"add", "Test"});
    }

    @Test
    public void test_commit() throws Exception {
        Command.InitCommand.callInit(new Repository("Test"), new String[0]);
        File testFile = Utils.join(System.getProperty("user.dir"), "Test", "Test");
        FileOutputStream fos = new FileOutputStream(testFile);
        fos.write("This is version 1".getBytes());
        fos.close();
        Command.AddCommand.callAdd(new Repository("Test"), new String[]{"add", "Test"});
        Command.CommitCommand.callCommit(new Repository("Test"), new String[]{"commit", "Another Commit"});
    }

    @Test
    public void test_remove() throws Exception {
        test_commit();
        Command.RmCommand.callrm(new Repository("Test"), new String[]{"rm", "Test"});
    }

    @Test
    public void test_log() throws Exception {
        Command.InitCommand.callInit(new Repository("Test"), new String[0]);
        File testFile = Utils.join(System.getProperty("user.dir"), "Test", "Test");
        FileOutputStream fos = new FileOutputStream(testFile);
        fos.write("This is version 1".getBytes());
        fos.close();
        Command.AddCommand.callAdd(new Repository("Test"), new String[]{"add", "Test"});
        Command.CommitCommand.callCommit(new Repository("Test"), new String[]{"commit", "Another Commit"});
        fos = new FileOutputStream(testFile);
        fos.write("This is version 2".getBytes());
        fos.close();
        Command.AddCommand.callAdd(new Repository("Test"), new String[]{"add", "Test"});
        Command.CommitCommand.callCommit(new Repository("Test"), new String[]{"commit", "Third Commit"});
        Command.LogCommand.callLog(new Repository("Test"), new String[]{"log"});
    }

    @Test
    public void test_all() throws Exception {
        test_log();
        String newBranchName = "new";
        System.out.println("Branch");
        Command.BranchCommand.callBranch(new Repository("Test"), new String[]{"branch", newBranchName});
        System.out.println("Checkout");
        Command.CheckoutCommand.callCheckout(new Repository("Test"), new String[]{"checkout", newBranchName});
        File testFile = Utils.join(System.getProperty("user.dir"), "Test", "Test");
        FileOutputStream fos = new FileOutputStream(testFile);
        fos.write("This is version 3 in new branch".getBytes());
        fos.close();
        System.out.println("add");
        Command.AddCommand.callAdd(new Repository("Test"), new String[]{"add", "Test"});
        Command.CommitCommand.callCommit(new Repository("Test"), new String[]{"commit", "Commit in new branch"});
        Command.CheckoutCommand.callCheckout(new Repository("Test"), new String[]{"checkout", "master"});
        fos = new FileOutputStream(testFile);
        fos.write("This is version 3 in master".getBytes());
        fos.close();
        Command.AddCommand.callAdd(new Repository("Test"), new String[]{"add", "Test"});
        Command.CommitCommand.callCommit(new Repository("Test"), new String[]{"commit", "Commit in master branch"});
        Command.MergeCommand.callMerge(new Repository("Test"), new String[]{"merge", newBranchName});
        System.out.println("Global log:");
        File newFile = new File("Test/2");
        if (!newFile.exists()) {
            newFile.createNewFile();
        }
        fos = new FileOutputStream(newFile);
        fos.write("This is version 1 of file 2".getBytes());
        fos.close();
        System.out.println("Checkout Master");
        Command.AddCommand.callAdd(new Repository("Test"), new String[]{"add", "2"});
        Command.CommitCommand.callCommit(new Repository("Test"), new String[]{"commit", "Commit in master branch of file 2"});
        System.out.println("Separate");
        System.out.println("Checkout New");
        Command.CheckoutCommand.callCheckout(new Repository("Test"), new String[]{"checkout", newBranchName});
        Command.GlobalLogCommand.callGloballog(new Repository("Test"), new String[]{"global-log"});
    }

    @Test
    public void reproduction1() throws Exception {
        initialize();
        writeToFile("1", "1");
        writeToFile("2", "2");
        addFile(new String[]{"1", "2"});
        Command.CommitCommand.callCommit(new Repository("Test"), new String[]{"commit", "Commit in master branch of file 2"});
        Command.RmCommand.callrm(new Repository("Test"), new String[]{"rm", "2"});
        writeToFile("2", "2");
        addFile(new String[]{"2"});
        Command.StatusCommand.callStatus(new Repository("Test"), new String[]{"status"});
    }
}


