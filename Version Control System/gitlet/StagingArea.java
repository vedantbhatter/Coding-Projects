package gitlet;


import java.io.Serializable;
import java.util.HashMap;


/**
 * Staging Area Class.
 * @author Vedant Bhatter
 */

public class StagingArea implements Serializable {

    /** Stagina area add. */
    private HashMap<String, String> _stagingAreaAdd;
    /** Stagina area remove. */
    private HashMap<String, String> _stagingAreaRemove;

    /**
     * Constructor for StagingArea.
     */

    public StagingArea() {
        _stagingAreaAdd = new HashMap<>();
        _stagingAreaRemove = new HashMap<>();
    }





}
