package gitlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
public class Command {

    public static class InitCommand {
        public static void callInit(Repository repo, String[] args) throws Exception {
            try {
                repo.init();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        public static boolean correctInput(String[] args) {
            return args.length == 1;
        }
    }

    public static class AddCommand {
        public static void callAdd(Repository repo, String[] args) throws IOException,
                ClassNotFoundException {
            String fileName = args[1];
            File workingDir = repo.getWorkingDir();
            File addFile = Utils.join(workingDir.getAbsolutePath(), fileName);
            try {
                repo.addFile(addFile);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        public static boolean correctInput(String[] args) {
            return args.length == 2;
        }
    }

    public static class CommitCommand {

        public static void callCommit(Repository repo, String[] args) throws IOException,
                ClassNotFoundException {
            Index index = repo.getIndex();
            if (args.length < 2) {
                System.out.println("Please enter a commit message.");
                return;
            }
            if (args[1].equals(" ")) {
                System.out.println("Please enter a commit message.");
                return;
            }
            String message = args[1];
            if (index.getAddStage().size() == 0 && index.getRemoveStage().size() == 0) {
                System.out.println("No changes added to the commit.");
                return;
            }
            try {
                repo.commit(message);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        public static boolean correctInput(String[] args) {
            return args.length == 2;
        }
    }

    public static class RmCommand {
        public static void callrm(Repository repo, String[] args) throws Exception, IOException,
                ClassNotFoundException {
            if (args.length < 2) {
                System.out.println("Please enter a remove message.");
                return;
            }
            try {
                repo.remove(args[1]);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        public static boolean correctInput(String[] args) {
            return args.length == 2;
        }
    }

    public static class FindCommand {
        public static void callFind(Repository repo, String[] args) throws IOException, ClassNotFoundException {
            if (args.length < 2) {
                System.out.println("Please enter a find message.");
                return;
            }
            if (args[1].equals(" ")) {
                System.out.println("Found no commit with that message.");
                return;
            }
            String commitMessgae = args[1];
            Commit currentCommit = repo.getCurrentCommit();
            int count = 0;
            while (currentCommit.getParent() != null) {
                String thisMessage = currentCommit.getMessage();
                if (commitMessgae.equals(thisMessage)) {
                    System.out.println(currentCommit.sha());
                    count++;
                }
                currentCommit = currentCommit.getParentCommit(repo);
            }
            if (count == 0) {
                throw new IllegalArgumentException("Found no commit with that message.");
            }
        }
    }

    public static class LogCommand {
        public static void callLog(Repository repo, String[] args) throws IOException,
                ClassNotFoundException {
            repo.log();
        }

        public static boolean correctInput(String[] args) {
            return args.length == 1;
        }
    }

    public static class GlobalLogCommand {
        public static void callGloballog(Repository repo, String[] args) throws IOException,
                ClassNotFoundException {
            repo.globalLog();
        }
        public static boolean correctInput(String[] args) {
            return args.length == 1;
        }
    }

    public static class StatusCommand {

        public static void callStatus(Repository repo, String[] args) throws IOException,
                ClassNotFoundException {
            repo.status();
        }
        public static boolean correctInput(String[] args) {
            return args.length == 1;
        }
    }

    public static class CheckoutCommand {
        public static void callCheckout(Repository repo, String[] args) {
            try {
                switch (args.length) {
                    case 3:
                        if (!args[1].equals("--")) {
                            throw new Exception("Invalid Argument for checkout.");
                        }
                        repo.checkoutFileWithCurrentCommit(args[2]);
                        break;
                    case 4:
                        if (!args[2].equals("--")) {
                            throw new Exception("Invalid Argument for checkout.");
                        }
                        repo.checkoutFileWithCommit(args[3], args[1]);
                        break;
                    case 2:
                        repo.checkoutBranch(args[1]);
                        break;
                    default:
                        throw new Exception("Invalid Argument for checkout.");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static class BranchCommand {
        public static void callBranch(Repository repo, String[] args) {
            if (args.length < 2) {
                System.out.println("Please enter a branch name.");
            }
            try {
                repo.branch(args[1]);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static class RmBranchCommand {
        public static void callRmBranch(Repository repo, String[] args) {
            if (args.length < 2) {
                System.out.println("Please enter a branch name.");
            }
            try {
                repo.rmBranch(args[1]);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static class ResetCommand {
        public static void callReset(Repository repo, String[] args) {
            if (args.length != 2) {
                System.out.println("Invalid argument for reset. Usage: " +
                        "java gitlet.Main reset [commit id]");
            }
            try {
                repo.reset(args[1], true);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static class MergeCommand {
        public static void callMerge(Repository repo, String[] args) {
            if (args.length != 2) {
                System.out.println("Invalid argument for merge. Usage: " +
                        "java gitlet.Main merge [branch name]");
            }
            try {
                repo.merge(args[1]);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
