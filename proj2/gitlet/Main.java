package gitlet;

import java.util.ArrayList;

/* Driver class for Gitlet, the tiny stupid version-control system.
   @author
*/
public class Main {

    /* Usage: java gitlet.Main ARGS, where ARGS contains
       <COMMAND> <OPERAND> .... */
    public static void main(String... args) throws Exception {
        ArrayList<String> instruction = new ArrayList<String>();
        instruction.add("init");
        instruction.add("add");
        instruction.add("commit");
        instruction.add("branch");
        instruction.add("checkout");
        instruction.add("merge");
        instruction.add("rm");
        instruction.add("log");
        instruction.add("global-log");
        instruction.add("rm-branch");
        instruction.add("reset");
        instruction.add("status");

        if (args == null || args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        String command = args[0];
        if (!instruction.contains(command)) {
            System.out.println("No command with that name exists.");
            return;
        }

        if (args.length < 0 || args.length > 4) {
            throw new IllegalArgumentException("Incorrect operands.");
        }
        Repository repository = new Repository(System.getProperty("user.dir"));
        if (command.equals("init")) {
            Command.InitCommand.callInit(repository, args);
        }
        if (command.equals("add")) {
            Command.AddCommand.callAdd(repository, args);
        }
        if (command.equals("commit")) {
            Command.CommitCommand.callCommit(repository, args);
        }
        if (command.equals("branch")) {
            Command.BranchCommand.callBranch(repository, args);
        }
        if (command.equals("checkout")) {
            Command.CheckoutCommand.callCheckout(repository, args);
        }
        if (command.equals("merge")) {
            Command.MergeCommand.callMerge(repository, args);
        }
        if (command.equals("rm")) {
            Command.RmCommand.callrm(repository, args);
        }
        if (command.equals("log")) {
            Command.LogCommand.callLog(repository, args);
        }
        if (command.equals("global-log")) {
            Command.GlobalLogCommand.callGloballog(repository, args);
        }
        if (command.equals("rm-branch")) {
            Command.RmBranchCommand.callRmBranch(repository, args);
        }
        if (command.equals("reset")) {
            Command.ResetCommand.callReset(repository, args);
        }
        if (command.equals("status")) {
            Command.StatusCommand.callStatus(repository, args);
        }
    }

}
