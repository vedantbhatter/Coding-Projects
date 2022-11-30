package enigma;




import java.util.HashMap;




/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Vedant Bhatter
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
        _cycles = _cycles.replace("(", "").replace(")", "");
        resultStringList = _cycles.split(" ");
        permute = new HashMap<>();
        invert = new HashMap<>();
        deranged = true;

        for (String s1 : resultStringList) {
            if (s1.length() == 1) {
                deranged = false;
            } else if (s1.length() == 0) {
                break;
            }
            s1 += s1.charAt(0);
            for (int c = 0; c < s1.length() - 1; c++) {
                permute.put(s1.charAt(c), s1.charAt(c + 1));
                invert.put(s1.charAt(s1.length() - 1 - c),
                        s1.charAt(s1.length() - 2 - c));
            }
        }

        String s = alphabet.getChars();
        for (int j = 0; j < s.length(); j++) {
            if (!permute.containsKey(s.charAt(j))) {
                permute.put(s.charAt(j), s.charAt(j));
                invert.put(s.charAt(j), s.charAt(j));
                deranged = false;
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char c1 = alphabet().getChars().charAt(p);
        char permuted = permute(c1);
        return alphabet().getChars().indexOf(permuted);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char c1 = alphabet().getChars().charAt(c);
        char inverted = invert(c1);
        return alphabet().getChars().indexOf(inverted);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        String alphab = alphabet().getChars();
        if (alphab.indexOf(p) == -1) {
            throw new EnigmaException("not in alphabet");
        }
        return permute.get(p);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        String alphab = alphabet().getChars();
        if (alphab.indexOf(c) == -1) {
            throw new EnigmaException("Not in alphabet");
        }
        return invert.get(c);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        return deranged;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** String of Cycles. */
    private String _cycles;

    /** Hashmap of permute. */
    private HashMap<Character, Character> permute;

    /** Hashmap of invert. */
    private HashMap<Character, Character> invert;

    /** resulting String list. */
    private String [] resultStringList;
    /** boolean to check for deranged. */
    private boolean deranged;
}
