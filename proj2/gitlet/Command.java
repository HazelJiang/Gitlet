package gitlet;

import java.io.File;

public class Command  {

    public static class InitCommand {
        public void callInit(Repository repo, String[] args) {
            repo.init();
        }
        public boolean correctInput(String[] args) {
            return args.length == 1;
        }
    }

    public static class AddCommand {
        public void callAdd(Repository repo, String[] args) {
            String fileName = args[0];
            File workingDir = repo.getWorkingDir();



        }
        public boolean correctInput(String[] args) {
            return args.length == 2;
        }
    }
    public static class CommitCommand {

        public void callCommit(Repository repo, String[] args) {
        }
        public boolean correctInput(String[] args) {
            return args.length == 2;
        }
    }
    public static class RmCommand {
        public void callrm(Repository repo, String[] args) {
        }
        public boolean correctInput(String[] args) {
            return args.length == 2;
        }
    }
    public static class LogCommand {
        public void callLog(Repository repo, String[] args) {
        }
        public boolean correctInput(String[] args) {
            return args.length == 1;
        }
    }
    public static class GlobalLogCommand {
        public void callGloballog(Repository repo, String[] args) {
        }
        public boolean correctInput(String[] args) {
            return args.length == 1;
        }
    }
    public static class FindCommand {

        public void callFind(Repository repo, String[] args) {
        }
        public boolean correctInput(String[] args) {
            return args.length == 2;
        }
    }
    public static class StatusCommand {

        public void callStatus(Repository repo, String[] args) {
        }
        public boolean correctInput(String[] args) {
            return args.length == 1;
        }
    }
    public static class CheckOutCommand {

        public void callCheckout(Repository repo, String[] args) {
        }
        public boolean correctInput(String[] args){}
    }
    public static class BranchCommand {

        public void callBranch(Repository repo, String[] args) {
        }
        public boolean correctInput(String[] args){}
    }
    public static class RmBranchCommand {

        public void callRmBranch(Repository repo, String[] args) {
        }
        public boolean correctInput(String[] args){}
    }
    public static class ResetCommand {

        public void callReset(Repository repo, String[] args) {
        }
        public boolean correctInput(String[] args){}
    }
    public static class MergeCommand {

        public void callMerge(Repository repo, String[] args) {}
        public boolean correctInput(String[] args){}

    }


}
