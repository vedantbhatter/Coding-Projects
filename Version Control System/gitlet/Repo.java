package gitlet;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.LinkedList;
import java.util.Queue;

/** Has all gitlet commands and helper methods.
 * @author Vedant Bhatter
 */

public class Repo implements Serializable {

    /**
     * All gitlet commands.
     *
     * @author Vedant Bhatter
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * Gitlet directory.
     */
    public static final File GITLET = Utils.join(CWD, ".gitlet");
    /**
     * All the commit files.
     */
    public static final File COMMIT = Utils.join(GITLET, "commit");
    /**
     * All branch files.
     */
    public static final File BRANCH = Utils.join(GITLET, "branch");
    /**
     * All blob files.
     */
    public static final File BLOB = Utils.join(GITLET, "blob");

    /* Creates a new Gitlet version-control system in the current directory
     * The system will automatically start with
     * one commit: a commit that contains no files and has the
     * message "initial commit". It will have a single
     * branch, called "master", which initially
     * points to this initial commit and master will be the current branch.
     */

    /**
     * Init command.
     */
    public static void init() {
        if (GITLET.exists()) {
            System.out.println(" A Gitlet version-control system already "
                    + "exists in the current directory.");
            System.exit(0);
        }
        GITLET.mkdir();
        COMMIT.mkdir();
        BRANCH.mkdir();
        BLOB.mkdir();

        Commit commit = new Commit("initial commit",
                null, null, new Date(0), new HashMap<>());
        Utils.writeObject(Utils.join(COMMIT, commit.getId()), commit);
        Branch branch = new Branch("master", commit.getId());
        Utils.writeObject(Utils.join(BRANCH, "master"), branch);

        File currBranch = Utils.join(CWD, "currentBranch");
        Utils.writeContents(currBranch, "master");
    }

    /**
     * Add command.
     *
     * @param file which is added to the staging area.
     */
    public static void add(String file) {
        File f = new File(file);
        if (!f.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        String id = Utils.sha1(Utils.readContents(f));
        Branch currBranch = getCurrBranch();
        Commit currCommit = Utils.readObject(Utils.join(COMMIT,
                currBranch.getHead()), Commit.class);
        File blob = Utils.join(BLOB, id);

        if (currCommit.getFiles().containsKey(file)
                && currCommit.getFiles().get(file).equals(id)) {
            currBranch.clearFile(file);
        } else {
            currBranch.addFile(file, id);
            Utils.writeContents(blob, Utils.readContents(f));
        }
        Utils.writeObject(Utils.join(BRANCH, currBranch.getName()), currBranch);

    }

    /**
     * Current branch command which gets the current branch.
     *
     * @return the current branch.
     */
    private static Branch getCurrBranch() {
        String name = Utils.readContentsAsString(
                Utils.join(CWD, "currentBranch"));
        return Utils.readObject(Utils.join(BRANCH, name), Branch.class);
    }

    /**
     * Commit command.
     *
     * @param message of the commit.
     */
    public static void commit(String message) {
        if (message == null || message.equals("")) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        Branch currBranch = getCurrBranch();
        if (currBranch.getStagingAreaAdd().size() == 0
                && currBranch.getStagingAreaRemove().size() == 0) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit currCommit = Utils.readObject(Utils.join(COMMIT,
                currBranch.getHead()), Commit.class);
        Commit commit = new Commit(message, currBranch.getHead(),
                new Date(), currCommit.getFiles(),
                currBranch.getStagingAreaAdd(),
                currBranch.getStagingAreaRemove());
        Utils.writeObject(Utils.join(COMMIT, commit.getId()), commit);
        currBranch.setHead(commit.getId());
        currBranch.clearStagingArea();
        Utils.writeObject(Utils.join(BRANCH, currBranch.getName()), currBranch);
    }

    /**
     * Log command.
     */
    public static void log() {
        Branch currBranch = getCurrBranch();
        Commit currCommit = Utils.readObject(Utils.join(COMMIT,
                currBranch.getHead()), Commit.class);
        while (currCommit != null) {
            System.out.println("===");
            System.out.println("commit " + currCommit.getId());
            SimpleDateFormat sdf =
                    new SimpleDateFormat("EE MMM dd hh:mm:ss yyyy Z");
            System.out.println("Date: " + sdf.format(currCommit.getDate()));
            System.out.println(currCommit.getMessage());
            System.out.println();
            if (currCommit.getParent() == null) {
                break;
            }
            currCommit = Utils.readObject(Utils.join(COMMIT,
                    currCommit.getParent()), Commit.class);
        }
    }

    /**
     * Global log command.
     */
    public static void globalLog() {
        for (File commit : COMMIT.listFiles()) {
            Commit c = Utils.readObject(commit, Commit.class);
            System.out.println("===");
            System.out.println("commit " + c.getId());
            SimpleDateFormat sdf =
                    new SimpleDateFormat("EE MMM dd hh:mm:ss yyyy Z");
            System.out.println("Date: " + sdf.format(c.getDate()));
            System.out.println(c.getMessage());
            System.out.println();
        }
    }

    /**
     * Find command.
     *
     * @param commitMsg which is the commit message.
     */
    public static void find(String commitMsg) {
        List<String> commitIds = Utils.plainFilenamesIn(COMMIT);
        if (commitIds.size() == 0) {
            return;
        }
        boolean bool = false;
        for (String id : commitIds) {
            Commit c = Utils.readObject(Utils.join(COMMIT, id), Commit.class);
            if (c.getMessage().equals(commitMsg)) {
                System.out.println(c.getId());
                bool = true;
            }
        }
        if (!bool) {
            System.out.println("Found no commit with that message.");
        }
    }

    /**
     * Status command.
     */
    public static void status() {
        if (!GITLET.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        Branch currBranch = getCurrBranch();
        System.out.println("=== Branches ===");
        for (String branch : Utils.plainFilenamesIn(BRANCH)) {
            if (branch.equals(currBranch.getName())) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        HashMap<String, String> currStaged = currBranch.getStagingAreaAdd();
        for (String file : currStaged.keySet()) {
            System.out.println(file);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        HashMap<String, String> removed = currBranch.getStagingAreaRemove();
        for (String file : removed.keySet()) {
            System.out.println(file);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        untrackedHelper();
        System.out.println();

    }

    /**
     * Checkout head command.
     *
     * @param fileName which is the file name.
     */
    public static void checkoutHead(String fileName) {
        Branch currBranch = getCurrBranch();
        Commit currCommit = Utils.readObject(Utils.join(COMMIT,
                currBranch.getHead()), Commit.class);
        File file = new File(fileName);
        if (!currCommit.getFiles().containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String id = currCommit.getFiles().get(fileName);
        File blob = Utils.join(BLOB, id);
        Utils.writeContents(file, Utils.readContents(blob));
    }

    /**
     * Checkout commit command.
     *
     * @param commitId commit id.
     * @param fileName file name.
     */
    public static void checkoutCommit(String commitId, String fileName) {
        if (commitId.length() < Utils.UID_LENGTH) {
            for (String smallID : Utils.plainFilenamesIn(COMMIT)) {
                if (smallID.contains(commitId)) {
                    commitId = smallID;
                    break;
                }
            }
        }
        if (!Utils.plainFilenamesIn(COMMIT).contains(commitId)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit commit = Utils.readObject(Utils.join(COMMIT,
                commitId), Commit.class);
        if (commit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        File file = new File(fileName);
        if (!commit.getFiles().containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String id = commit.getFiles().get(fileName);
        File blob = Utils.join(BLOB, id);
        Utils.writeContents(file, Utils.readContents(blob));
    }

    /**
     * Keeps track of untracked files.
     *
     * @return untracked file.
     */
    public static ArrayList<String> untrackedHelper() {
        ArrayList<String> untracked = new ArrayList<String>();
        Branch currBranch = getCurrBranch();
        Commit headCommit = Utils.readObject(Utils.join(COMMIT,
                currBranch.getHead()), Commit.class);
        for (String f : Objects.requireNonNull(Utils.plainFilenamesIn(CWD))) {
            if (f.equals("currentBranch")) {
                continue;
            }
            if (!currBranch.getStagingAreaAdd().containsKey(f)
                    && !headCommit.getFiles().containsKey(f)) {
                untracked.add(f);
            }
        }
        return untracked;
    }

    /**
     * Checkout branch command.
     *
     * @param branchName which needs to get checked out.
     */

    public static void checkoutBranch(String branchName) {
        Branch currBranch = getCurrBranch();
        if (currBranch.getName().equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        if (!Objects.requireNonNull(Utils.plainFilenamesIn(BRANCH)).
                contains(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (!untrackedHelper().isEmpty()) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it, or add and commit it first.");
            System.exit(0);
        }
        Branch branchNew = Utils.readObject(Utils.join(BRANCH,
                branchName), Branch.class);
        Utils.writeContents(Utils.join(CWD, "currentBranch"), branchName);
        for (String file : Utils.plainFilenamesIn(CWD)) {
            if (file.equals("currentBranch")) {
                continue;
            }
            Utils.restrictedDelete(file);
        }
        Commit currCommit = Utils.readObject(Utils.join(COMMIT,
                branchNew.getHead()), Commit.class);
        for (String file : currCommit.getFiles().keySet()) {
            String id = currCommit.getFiles().get(file);
            File blob = Utils.join(BLOB, id);
            File fileNew = new File(file);
            Utils.writeContents(fileNew, Utils.readContents(blob));
        }
    }

    /**
     * Remove command.
     *
     * @param fileName which needs to get removed.
     */

    public static void rm(String fileName) {
        Branch currBranch = getCurrBranch();
        Commit currCommit = Utils.readObject(Utils.join(COMMIT,
                currBranch.getHead()), Commit.class);
        if (!currBranch.getStagingAreaAdd().containsKey(fileName)
                && !currCommit.getFiles().containsKey(fileName)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        currBranch.getStagingAreaAdd().remove(fileName);
        if (currCommit.getFiles().containsKey(fileName)) {
            currBranch.removeFile(fileName,
                    currCommit.getFiles().get(fileName));
            Utils.restrictedDelete(fileName);
        }
        Utils.writeObject(Utils.join(BRANCH, currBranch.getName()), currBranch);
    }

    /**
     * Branch command.
     *
     * @param branchName where a new branch gets created.
     */
    public static void branch(String branchName) {
        for (File f : BRANCH.listFiles()) {
            if (f.getName().equals(branchName)) {
                System.out.println("A branch with that name already exists.");
                System.exit(0);
            }
        }
        Branch branch = new Branch(branchName, getCurrBranch().getHead());
        Utils.writeObject(Utils.join(BRANCH, branchName), branch);
    }

    /**
     * Remove branch command.
     *
     * @param branchName which is the branch that needs to get removed.
     */
    public static void rmBranch(String branchName) {
        if (!Utils.plainFilenamesIn(BRANCH).contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (branchName.equals(getCurrBranch().getName())) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        Utils.join(BRANCH, branchName).delete();
    }

    /**
     * Reset command.
     *
     * @param commitId which is the commitID which will get reset to.
     */
    public static void reset(String commitId) {
        if (!Utils.plainFilenamesIn(COMMIT).contains(commitId)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        if (!untrackedHelper().isEmpty()) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it, or add and commit it first.");
            System.exit(0);
        }
        Commit currCommit = Utils.readObject(Utils.join(COMMIT, commitId),
                Commit.class);
        Branch currBranch = getCurrBranch();

        for (String f : (Utils.plainFilenamesIn(CWD))) {
            if (f.equals("currentBranch")) {
                continue;
            }
            Utils.restrictedDelete(f);
        }
        for (String f : currCommit.getFiles().keySet()) {
            checkoutCommit(commitId, f);
        }
        currBranch.setHead(commitId);
        currBranch.clearStagingArea();
        Utils.writeObject(Utils.join(BRANCH, currBranch.getName()), currBranch);


    }


    /**
     * Splitpoint method .
     *
     * @param branch  branch one
     * @param branch2 branch two.
     * @return the split point.
     */
    public static String splitPoint(String branch, String branch2) {
        if (!Utils.plainFilenamesIn(BRANCH).contains(branch)
                || !Utils.plainFilenamesIn(BRANCH).contains(branch2)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        Branch firstBranch = Utils.readObject(Utils.join(BRANCH, branch),
                Branch.class);
        Branch secondBranch = Utils.readObject(Utils.join(BRANCH, branch2),
                Branch.class);
        Commit firstCommit = Utils.readObject(Utils.join(COMMIT,
                firstBranch.getHead()), Commit.class);
        Commit secondCommit = Utils.readObject(Utils.join(COMMIT,
                secondBranch.getHead()), Commit.class);
        HashSet<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(firstCommit.getId());
        while (!queue.isEmpty()) {
            String curr = queue.poll();
            visited.add(curr);
            Commit currCommit = Utils.readObject(Utils.join(COMMIT, curr),
                    Commit.class);
            if (currCommit.getParent() != null) {
                queue.add(currCommit.getParent());
            }
            if (currCommit.getParent2() != null) {
                queue.add(currCommit.getParent2());
            }
        }
        Queue<String> queue2 = new LinkedList<>();
        queue2.add(secondCommit.getId());
        while (!queue2.isEmpty()) {
            String curr = queue2.poll();
            if (visited.contains(curr)) {
                return curr;
            }
            Commit currCommit = Utils.readObject(Utils.join(COMMIT, curr),
                    Commit.class);
            if (currCommit.getParent() != null) {
                queue2.add(currCommit.getParent());
            }
            if (currCommit.getParent2() != null) {
                queue2.add(currCommit.getParent2());
            }
        }
        return null;
    }

    /**
     * Merge command.
     *
     * @param branchName name of the branch.
     */
    public static void merge(String branchName) {
        boolean con = false;
        String split = splitPoint(getCurrBranch().getName(), branchName);
        Commit splitCommit = Utils.readObject(Utils.join(COMMIT, split),
                Commit.class);
        Branch currBranch = getCurrBranch();
        Branch givenBranch = Utils.readObject(Utils.join(BRANCH, branchName),
                Branch.class);
        Commit currCommit = Utils.readObject(Utils.join(COMMIT,
                currBranch.getHead()), Commit.class);
        Commit givenCommit = Utils.readObject(Utils.join(COMMIT,
                givenBranch.getHead()), Commit.class);
        HashMap<String, String> currFiles = currCommit.getFiles();
        HashMap<String, String> givenFiles = givenCommit.getFiles();
        HashMap<String, String> splitFiles = splitCommit.getFiles();
        mergeErrors(branchName);
        for (String f : splitFiles.keySet()) {
            mergeHelper1(f, branchName);
        }
        for (String f : currFiles.keySet()) {
            if (!givenFiles.containsKey(f) && splitFiles.containsKey(f)) {
                if (!currFiles.get(f).equals(splitFiles.get(f))) {
                    con = mergeConflict(Utils.readContentsAsString(
                            Utils.join(BLOB, currFiles.get(f))), "", f);
                } else {
                    rmMerge(f);
                }
            }
            mergeHelperTwo(f, branchName);
        }
        for (String f : givenFiles.keySet()) {
            if (!splitFiles.containsKey(f) && !currFiles.containsKey(f)) {
                File newFile = Utils.join(CWD, f);
                File cFile = Utils.join(BLOB, givenFiles.get(f));
                Utils.writeContents(newFile, Utils.readContentsAsString(cFile));
                currBranch.addFile(f, givenFiles.get(f));
            }
            if (currFiles.containsKey(f) && !splitFiles.containsKey(f)) {
                if (!currFiles.get(f).equals(givenFiles.get(f))) {
                    con = mergeConflict(Utils.readContentsAsString(Utils.join(
                            BLOB, currFiles.get(f))), Utils.readContentsAsString
                            (Utils.join(BLOB, givenFiles.get(f))), f);
                }
            } else if (currFiles.containsKey(f) && splitFiles.containsKey(f)) {
                if (!currFiles.get(f).equals(givenFiles.get(f))
                        && !currFiles.get(f).equals(splitFiles.get(f))
                        && !givenFiles.get(f).equals(splitFiles.get(f))) {
                    con = mergeConflict(Utils.readContentsAsString(Utils.join(
                            BLOB, currFiles.get(f))), Utils.readContentsAsString
                            (Utils.join(BLOB, givenFiles.get(f))), f);
                }
            }
        }
        if (con) {
            System.out.println("Encountered a merge conflict.");
        }
        mergeCommit(currBranch, givenBranch);
        currBranch.setMerged(givenBranch.getName(), true);
        Utils.writeObject(Utils.join(BRANCH, currBranch.getName()), currBranch);
    }

    /**
     * Commit for merge.
     *
     * @param currBranch  current branch.
     * @param givenBranch given branch.
     */
    private static void mergeCommit(Branch currBranch, Branch givenBranch) {
        String message = "Merged " + givenBranch.getMergeBranch()
                + " into " + currBranch.getName() + ".";
        Commit currCommit = Utils.readObject(Utils.join(COMMIT,
                currBranch.getHead()), Commit.class);
        Commit commit = new Commit(message, currBranch.getHead(),
                givenBranch.getHead(),
                new Date(), currCommit.getFiles(),
                currBranch.getStagingAreaAdd(),
                currBranch.getStagingAreaRemove());
        Utils.writeObject(Utils.join(COMMIT, commit.getId()), commit);
        currBranch.setHead(commit.getId());
        currBranch.clearStagingArea();
    }

    /**
     * Add method for merge.
     *
     * @param file which will be added to the staging area.
     */
    private static void addMerge(String file) {
        File f = Utils.join(CWD, file);
        String id = Utils.sha1(Utils.readContents(f));
        Branch currBranch = getCurrBranch();
        String contents = Utils.readContentsAsString(f);
        Commit currCommit = Utils.readObject(Utils.join(COMMIT,
                currBranch.getHead()), Commit.class);
        File blob = Utils.join(BLOB, id);

        if (currCommit.getFiles().containsKey(file)
                && currCommit.getFiles().get(file).equals(id)) {
            currBranch.clearFile(file);
        } else {
            currBranch.addFile(file, id);
            Utils.writeContents(blob, Utils.readContents(f));
        }
    }

    /**
     * Remove method but for merge.
     *
     * @param fileName which will be removed.
     */
    public static void rmMerge(String fileName) {
        Branch currBranch = getCurrBranch();
        Commit currCommit = Utils.readObject(Utils.join(COMMIT,
                currBranch.getHead()), Commit.class);
        if (!currBranch.getStagingAreaAdd().containsKey(fileName)
                && !currCommit.getFiles().containsKey(fileName)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }

        currBranch.getStagingAreaAdd().remove(fileName);
        if (currCommit.getFiles().containsKey(fileName)) {
            currBranch.removeFile(fileName,
                    currCommit.getFiles().get(fileName));
            Utils.restrictedDelete(fileName);
        }
    }

    /**
     * Merge conflict method.
     *
     * @param currString  string.
     * @param givenString string.
     * @param f           file.
     * @return boolean.
     */
    public static Boolean mergeConflict(String currString,
                                        String givenString, String f) {
        String conflictContents = "<<<<<<< HEAD\n"
                + currString + "=======\n" + givenString + ">>>>>>>\n";
        File conflictFile = Utils.join(CWD, f);
        Utils.writeContents(conflictFile, conflictContents);
        addMerge(f);
        return true;
    }

    /**
     * Handles all error casses for merge.
     *
     * @param branchName is the name of branch.
     */
    public static void mergeErrors(String branchName) {
        String split = splitPoint(getCurrBranch().getName(), branchName);
        Commit splitCommit = Utils.readObject(Utils.join(COMMIT, split),
                Commit.class);
        Branch currBranch = getCurrBranch();
        Branch givenBranch = Utils.readObject(Utils.join(BRANCH, branchName),
                Branch.class);
        Commit currCommit = Utils.readObject(Utils.join(COMMIT,
                currBranch.getHead()), Commit.class);
        Commit givenCommit = Utils.readObject(Utils.join(COMMIT,
                givenBranch.getHead()), Commit.class);
        HashMap<String, String> currFiles = currCommit.getFiles();
        HashMap<String, String> givenFiles = givenCommit.getFiles();
        HashMap<String, String> splitFiles = splitCommit.getFiles();
        if (currBranch.getName().equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        if (!Utils.plainFilenamesIn(BRANCH).contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (currBranch.getStagingAreaAdd().size() != 0
                || currBranch.getStagingAreaRemove().size() != 0) {
            System.out.println("You have uncommitted changes. ");
            System.exit(0);
        }
        if (!untrackedHelper().isEmpty()) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it, or add and commit it first.");
            System.exit(0);
        }
        if (split.equals(givenBranch.getHead())) {
            System.out.println("Given branch is an "
                    + "ancestor of the current branch.");
            System.exit(0);
        }
        if (split.equals(currBranch.getHead())) {
            reset(givenBranch.getHead());
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
    }

    /** Merge helper one.
     *
     * @param f is the file.
     * @param branchName is the branch name.
     */
    public static void mergeHelper1(String f, String branchName) {
        String split = splitPoint(getCurrBranch().getName(), branchName);
        Commit splitCommit = Utils.readObject(Utils.join(COMMIT, split),
                Commit.class);
        Branch currBranch = getCurrBranch();
        Branch givenBranch = Utils.readObject(Utils.join(BRANCH, branchName),
                Branch.class);
        Commit currCommit = Utils.readObject(Utils.join(COMMIT,
                currBranch.getHead()), Commit.class);
        Commit givenCommit = Utils.readObject(Utils.join(COMMIT,
                givenBranch.getHead()), Commit.class);
        HashMap<String, String> currFiles = currCommit.getFiles();
        HashMap<String, String> givenFiles = givenCommit.getFiles();
        HashMap<String, String> splitFiles = splitCommit.getFiles();
        if (currFiles.containsKey(f) && !givenFiles.containsKey(f)) {
            if (currFiles.get(f).equals(splitFiles.get(f))) {
                Utils.restrictedDelete(Utils.join(CWD, f));
            }
        }
    }

    /** Merge helper 2.
     *
     * @param f is the file.
     * @param branchName name of the branch.
     */
    public static void mergeHelperTwo(String f, String branchName) {
        boolean con = false;
        String split = splitPoint(getCurrBranch().getName(), branchName);
        Commit splitCommit = Utils.readObject(Utils.join(COMMIT, split),
                Commit.class);
        Branch currBranch = getCurrBranch();
        Branch givenBranch = Utils.readObject(Utils.join(BRANCH, branchName),
                Branch.class);
        Commit currCommit = Utils.readObject(Utils.join(COMMIT,
                currBranch.getHead()), Commit.class);
        Commit givenCommit = Utils.readObject(Utils.join(COMMIT,
                givenBranch.getHead()), Commit.class);
        HashMap<String, String> currFiles = currCommit.getFiles();
        HashMap<String, String> givenFiles = givenCommit.getFiles();
        HashMap<String, String> splitFiles = splitCommit.getFiles();
        if (splitFiles.containsKey(f) && givenFiles.containsKey(f)) {
            if (!givenFiles.get(f).equals(currFiles.get(f))
                    && currFiles.get(f).equals(splitFiles.get(f))) {
                String givenString = Utils.readContentsAsString(Utils.join(
                        BLOB, givenFiles.get(f)));
                Utils.writeContents(Utils.join(CWD, f), givenString);
                addMerge(f);
            }
        }
    }
}
