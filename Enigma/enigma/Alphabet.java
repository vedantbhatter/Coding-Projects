package enigma;


/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Vedant Bhatter
 */
class Alphabet {

    /** It is a string. */
    private String _chars;

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _chars = chars;
        if (_chars.contains(" ")) {
            throw new EnigmaException(
                    "White space not allowed, not valid Alphabet");
        }
        if (_chars.isEmpty()) {
            throw new EnigmaException("Empty Alphabet, not valid Alphabet");
        }

        if (_chars.contains("*")) {
            throw new EnigmaException("Cannot contain *, not valid Alphabet");
        }

        if (_chars.contains("(") || _chars.contains(")")) {
            throw new EnigmaException("Cannot contain ( "
                    + "or ), not valid Alphabet");
        }

    }


    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _chars.length();
    }

    /** Get the chars.
     * @return a character */
    String getChars() {
        return this._chars;
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        int i = 0;
        int len = _chars.length();
        while (i < len) {
            if (ch == _chars.charAt(i)) {
                return true;
            }
            i += 1;
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= IN
     *  DEX < size(). */
    char toChar(int index) {
        if (index >= 0 && index < _chars.length()) {
            return _chars.charAt(index);
        }
        throw new EnigmaException("Ch not in alphabet / out of range");
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        int i = 0;
        int len = _chars.length();
        while (i < len) {
            if (ch == _chars.charAt(i)) {
                return i;
            }
            i += 1;
        }
        throw new EnigmaException("Ch not in alphabet / out of range");
    }

}
