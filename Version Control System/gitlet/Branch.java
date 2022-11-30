package gitlet;

import java.io.Serializable;
import java.util.HashMap;

/** Branch Class. /*
 * @author Vedant Bhatter
 */

public class Branch implements Serializable {

    /** Name of branch. */
    private String name;

    /** Head of the branch. */
    private String head;

    /** Hashmap of Staging Area where files get added in. */
    private HashMap<String, String> _stagingAreaAdd;

    /** Hashmap of Staging Area where files get removed from. */
    private HashMap<String, String> _stagingAreaRemove;


    /** Setter method for merge .
     *
     * @param mergeBranchs merge branch.
     * @param mergeds merged.
     */

    public void setMerged(String mergeBranchs, Boolean mergeds) {
        this.merged = mergeds;
        this.mergeBranch = mergeBranchs;
    }

    /** Boolean merged. */
    private Boolean merged;

    /** Getter for merge.
     *
     * @return boolean.
     */
    public Boolean getMerged() {
        return merged;
    }

    /** Getter method for merge.
     *
     * @return mergeBranch.
     */
    public String getMergeBranch() {
        return mergeBranch;
    }

    /** String method mergeBranch. */
    private String mergeBranch;

    /** The branch constructor for the branch class.
     *
     * @param nameBranch is the name of the branch.
     * @param headBranch is the head of the branch.
     */
    public Branch(String nameBranch, String headBranch) {
        this.name = nameBranch;
        this.head = headBranch;
        _stagingAreaAdd = new HashMap<>();
        _stagingAreaRemove = new HashMap<>();
        merged = false;
        mergeBranch = nameBranch;
    }

    /** Gets the head of the branch.
     *
     * @return head
     */
    public String getHead() {
        return head;
    }

    /** Removes the file from the staging area add.
     *
     * @param fileName is the name of the file in the hashmap.
     */
    public void clearFile(String fileName) {
        _stagingAreaAdd.remove(fileName);
        _stagingAreaRemove.remove(fileName);
    }


    /** Add the file to the staging area.
     *
     * @param file which is the file that is being added.
     * @param id is the ID of the file itself.
     */
    public void addFile(String file, String id) {
        _stagingAreaAdd.put(file, id);
        _stagingAreaRemove.remove(file);
    }

    /** For the rm command where the file is removed
     * from the added area to the removed area.
     *
     * @param file takes in the file.
     * @param id is the id of the file.
     */
    public void removeFile(String file, String id) {
        _stagingAreaAdd.remove(file);
        _stagingAreaRemove.put(file, id);
    }

    /** Unstages a ffile from the removal area.
     *
     * @param file name of the file.
     */
    public void unstageRemove(String file) {
        _stagingAreaRemove.remove(file);
    }

    /** Getter method to get the name of the branch.
     *
     * @return the name of the branch.
     */
    public String getName() {
        return name;
    }

    /** Setter method to set the head of the branch.
     *
     * @param id which is the ID of the branch commit.
     */
    public void setHead(String id) {
        head = id;
    }

    /** Getter method to get the current head of the branch.
     *
     * @param id which is the ID of the branch commit.
     */
    public void getHead(String id) {
        head = id;
    }

    /** Gets the Staging Area where the files are added.
     *
     * @return the staging area.
     */
    public HashMap<String, String> getStagingAreaAdd() {
        return _stagingAreaAdd;
    }

    /** Clears the staging area. */
    public void clearStagingArea() {
        _stagingAreaAdd.clear();
        _stagingAreaRemove.clear();
    }

    /** Get the staging area where the files are staged to be removed.
     *
     * @return the removed staging area.
     */
    public HashMap<String, String> getStagingAreaRemove() {
        return _stagingAreaRemove;
    }

    /** Reset command in the branch class.
     *
     * @param commitId which takes in the commit
     * id to reset back to a previous commit.
     */
    public void reset(String commitId) {
        _stagingAreaAdd.clear();
        _stagingAreaRemove.clear();
        head = commitId;
    }
}
