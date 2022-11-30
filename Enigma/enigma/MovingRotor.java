package enigma;



/** Class that represents a rotating rotor in the enigma machine.
 *  @author Vedant Bhatter
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initially in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    public String toString() {
        return "MovingRotor" + name();
    }

    @Override
    boolean atNotch() {
        int i = 0;
        int len = _notches.length();
        char notch = _notches.charAt(i);
        while (i < len) {
            if (setting() == alphabet().toInt(notch)) {
                return true;
            }
            i += 1;
        }
        return false;
    }


    @Override
    void advance() {
        int notch = 1 + setting();
        Permutation perm = permutation();
        set(perm.wrap(notch));
    }

    /** Private string for the notches. */
    private String _notches;
}
