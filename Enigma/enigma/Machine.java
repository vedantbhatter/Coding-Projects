package enigma;


import java.util.Collection;



/** Class that represents a complete enigma machine.
 *  @author Vedant Bhatter
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors.toArray();
        _rotors = new Rotor[_numRotors];
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        if (_rotors.length != _numRotors) {
            throw new EnigmaException("Length is not the same!");
        }

        for (int i = 0; i < rotors.length; i += 1) {
            for (Object rotor : _allRotors) {
                Rotor castRotor = (Rotor) rotor;
                if (rotors[i].equals(castRotor.name())) {
                    _rotors[i] = castRotor;
                }
            }
        }

        if (!(_rotors[_rotors.length - 1] instanceof MovingRotor)) {
            throw new EnigmaException(
                    "Must be a moving rotor not a fixed rotor!");
        }

        if (!(_rotors[0] instanceof Reflector)) {
            throw new EnigmaException(
                    "The first rotor must be a reflector rotor");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (numRotors() - 1 != setting.length()) {
            throw new EnigmaException("Lengths are not the same");
        }

        int i = 1;
        int rotorLen = _rotors.length;

        while (i < rotorLen) {
            char a = setting.charAt(i - 1);
            if (!_alphabet.contains(a)) {
                throw new EnigmaException("Not in alphabet, the setting");
            } else {
                _rotors[i].set(a);
            }
            i += 1;
        }

    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** New method to help advance the rotor in the convert int method. */
    private void advancingRotor() {
        boolean[] canRotate = new boolean[_rotors.length];
        for (int i = 0; i < numRotors(); i++) {
            canRotate[i] = (i == _rotors.length - 1)
                    || ((_rotors[i + 1].atNotch())
                            && _rotors[i].rotates());
            if (i < _rotors.length - 1
                    && canRotate[i]
                    && _rotors[i + 1].rotates()) {
                canRotate[i + 1] = true;
            }
        }

        for (int i = 0; i < numRotors(); i++) {
            if (canRotate[i]) {
                _rotors[i].advance();
            }
        }
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advancingRotor();
        int convertedResult = _plugboard.permute(c);
        int i = _rotors.length - (1);
        int counter = 1;
        int rotorLen = _rotors.length;

        while (i >= 0) {
            convertedResult = _rotors[i].convertForward(convertedResult);
            i -= 1;
        }
        while (counter < rotorLen) {
            convertedResult = _rotors[counter].convertBackward(convertedResult);
            counter += 1;
        }

        convertedResult = _plugboard.permute(convertedResult);

        return convertedResult;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String converted = "";
        int len = msg.length();
        int counter = 0;

        while (counter < len) {
            int a = _alphabet.toInt(msg.charAt(counter));
            converted += _alphabet.toChar(convert(a));
            counter += 1;
        }
        return converted;
    }

    /** Gets the rotors.
     * @return rotors
     * @param i */
    Rotor getRotor(int i) {
        return _rotors[i];
    }

    /** Gets the lastRotor length.
     * @return rotor len */
    Rotor getLastRotor() {
        return _rotors[_rotors.length - 1];
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of Rotors. */
    private int _numRotors;

    /** Number of Pawls. */
    private int _pawls;

    /** Plugboard Permutations. */
    private Permutation _plugboard;

    /** All possibilities of rotors. */
    private Object[] _allRotors;

    /** All rotors that are currently being used. */
    private Rotor[] _rotors;

}
