/*This method fulfills the function that merges files from the given branch into the current branch.
1. Any files that have been modified in the given branch since the split point, but not modified
in the current branch since the split point should be changed to their versions in the given branch
(checked out from the commit at the front of the given branch). These files should then all
be automatically staged. To clarify, if a file is “modified in the given branch since the split point” 
this means the version of the file as it exists in the commit at the front of the given branch has different
content from the version of the file at the split point.
2. Any files that have been modified in the current branch but not in the given branch
since the split point should stay as they are.
3. Any files that were not present at the split point and are present only in the current branch
should remain as they are.
4. Any files that were not present at the split point and are present only in the given branch should
be checked out and staged.
5. Any files present at the split point, unmodified in the current branch, and absent in the given branch
should be removed (and untracked).
6. Any files present at the split point, unmodified in the given branch, and absent in the current branch
should remain absent.
7. Any files modified in different ways in the current and given branches are in conflict.
“Modified in different ways” can mean that the contents of both are changed and different from other, 
or the contents of one are changed and the other is deleted, or the file was absent at the split point 
and have different contents in the given and current branches. In this case, replace the contents of the 
conflicted file with contents of file in given branch
 */
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
