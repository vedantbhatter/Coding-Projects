package gitlet;



/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Vedant Bhatter
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {

        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }

        switch (args[0]) {
        case "init":
            Repo.init();
            break;
        case "add":
            Repo.add(args[1]);
            break;
        case "commit":
            Repo.commit(args[1]);
            break;
        case "log":
            Repo.log();
            break;
        case "global-log":
            Repo.globalLog();
            break;
        case "status":
            Repo.status();
            break;
        case "find":
            Repo.find(args[1]);
            break;
        case "checkout":
            if (args.length == 2) {
                Repo.checkoutBranch(args[1]);
            } else if (args[1].equals("--")) {
                Repo.checkoutHead(args[2]);
            } else if (args[2].equals("--")) {
                Repo.checkoutCommit(args[1], args[3]);
            } else {
                System.out.println("Incorrect Operands.");
                System.exit(0);
            }
            break;
        case "rm":
            Repo.rm(args[1]);
            break;
        case "branch":
            Repo.branch(args[1]);
            break;
        case "rm-branch":
            Repo.rmBranch(args[1]);
            break;
        case "reset":
            Repo.reset(args[1]);
            break;
        case "merge":
            Repo.merge(args[1]);
            break;
        default:
            System.out.println("No command with that name exists.");
        }
    }


}
