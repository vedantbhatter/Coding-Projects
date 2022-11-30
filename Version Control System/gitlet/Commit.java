package gitlet;



import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;


/** Commit class.
 *  @author Vedant Bhatter
 */

public class Commit implements Serializable {
    /** The commit message. */
    private String _message;

    /** The parent commit. */
    private String _parent;

    /** Parent two. */
    private String _parent2;

    /** The commit ID. */
    private String _id;

    /** The commit time. */
    private Date _time;

    /** Tracked files. */
    private HashMap<String, String> _files;

    /** The commit branch. */
    private String _branch;


    /** Commit constructor.
     *  @param message which is the commit message.
     *  @param parent the parent commit.
     *  @param parent2 the second parent.
     *  @param time The commit time.
     *  @param file files which are tracked and will be committed.
     */

    public Commit(String message, String parent, String parent2, Date time,
                  HashMap<String, String> file) {
        _message = "initial commit";
        _parent = null;
        _parent2 = null;
        _id = Utils.sha1(time.toString() + message + parent + file + parent2);
        _time = time;
        _files = file;

    }

    /** Commit constructor part 2.
     *
     * @param message which is the commit message.
     * @param parent the parent commit.
     * @param time the time of the commit.
     * @param file files which are tracked and will be committed.
     * @param stagingArea the current staging area where the files are added.
     * @param stagingAreaRemoved the staging area where
     * the files are staged for removal.
     */
    public Commit(String message, String parent, Date time,
                  HashMap<String, String> file,
                  HashMap<String, String> stagingArea,
                  HashMap<String, String> stagingAreaRemoved) {
        _message = message;
        _parent = parent;
        _id = Utils.sha1(time.toString() + message + parent + file);
        _time = time;
        _files = file;
        _files.putAll(stagingArea);
        _files.keySet().removeAll(stagingAreaRemoved.keySet());
    }

    /** Merge commit command.
     *
     * @param message message.
     * @param parent parent.
     * @param parent2 parent2.
     * @param time time.
     * @param file file.
     * @param stagingArea area.
     * @param stagingAreaRemoved removed.
     */
    public Commit(String message, String parent, String parent2,
                            Date time,
                            HashMap<String, String> file,
                            HashMap<String, String> stagingArea,
                            HashMap<String, String> stagingAreaRemoved) {
        _message = message;
        _parent = parent;
        _parent2 = parent2;
        _id = Utils.sha1(time.toString() + message + parent + parent2 + file);
        _time = time;
        _files = file;
        _files.putAll(stagingArea);
        _files.keySet().removeAll(stagingAreaRemoved.keySet());
    }

    /** Get commit id.
     *
     * @return the id of the commit.
     */
    public String getId() {
        return _id;
    }

    /** Get commit message.
     *
     * @return the message of the commit.
     */
    public String getMessage() {
        return _message;
    }

    /** Gets the files from the staging area.
     *
     * @return the files.
     */
    public HashMap<String, String> getFiles() {
        return _files;
    }

    /** Get the parent commit.
     *
     * @return the parent commit.
     */
    public String getParent() {
        return _parent;
    }

    /** Get second parent commit.
     *
     * @return the second parent commit.
     */
    public String getParent2() {
        return _parent2;
    }

    /** Gets the time of the commit.
     *
     * @return the time of the commit.
     */
    public Date getDate() {
        return _time;
    }
}

