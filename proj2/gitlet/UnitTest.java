package gitlet;

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.io.FileOutputStream;

/* The suite of all JUnit tests for the gitlet package.
   @author
 */
public class UnitTest {

    /*@Test
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
        Command.BranchCommand.callBranch(new Repository("Test"), new String[]{"branch", newBranchName});
        Command.CheckoutCommand.callCheckout(new Repository("Test"), new String[]{"checkout", newBranchName});
        File testFile = Utils.join(System.getProperty("user.dir"), "Test", "Test");
        FileOutputStream fos = new FileOutputStream(testFile);
        fos.write("This is version 3 in new branch".getBytes());
        fos.close();
        Command.AddCommand.callAdd(new Repository("Test"), new String[]{"add", "Test"});
        Command.CommitCommand.callCommit(new Repository("Test"), new String[]{"commit", "Commit in new branch"});
        Command.CheckoutCommand.callCheckout(new Repository("Test"), new String[]{"checkout", "master"});
        fos = new FileOutputStream(testFile);
        fos.write("This is version 3 in master".getBytes());
        fos.close();
        Command.AddCommand.callAdd(new Repository("Test"), new String[]{"add", "Test"});
        Command.CommitCommand.callCommit(new Repository("Test"), new String[]{"commit", "Commit in master branch"});
        Command.MergeCommand.callMerge(new Repository("Test"), new String[]{"merge", newBranchName});
        Command.LogCommand.callLog(new Repository("Test"), new String[]{"log"});
        System.out.println("Global log:");
        Command.GlobalLogCommand.callGloballog(new Repository("Test"), new String[]{"global-log"});
        File newFile = new File("Test/2");
        if (!newFile.exists()) {
            newFile.createNewFile();
        }
        fos = new FileOutputStream(newFile);
        fos.write("This is version 1 of file 2".getBytes());
        fos.close();
        Command.CheckoutCommand.callCheckout(new Repository("Test"), new String[]{"checkout", "master"});
        Command.AddCommand.callAdd(new Repository("Test"), new String[]{"add", "2"});
        Command.CommitCommand.callCommit(new Repository("Test"), new String[]{"commit", "Commit in master branch of file 2"});
        System.out.println("Separate");
        Command.CheckoutCommand.callCheckout(new Repository("Test"), new String[]{"checkout", newBranchName});
    }*/
}


