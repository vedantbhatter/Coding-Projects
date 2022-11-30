package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Vedant Bhatter
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void testSize() {

        Permutation a = new Permutation("(A)", new Alphabet("A"));
        assertEquals(1, a.size());

        Permutation r =
                new Permutation("(GRL) (HI) (FN)", new Alphabet("HILFNGR"));
        assertEquals(7, r.size());
    }

    @Test
    public void testPermuteChar() {
        Permutation p = new Permutation("(BACD)", new Alphabet("(ABCD)"));
        assertEquals('A', p.permute('B'));
        assertEquals('C', p.permute('A'));
        assertEquals('D', p.permute('C'));
        assertEquals('B', p.permute('D'));

        Permutation a = new Permutation("(A)", new Alphabet("A"));
        assertEquals('A', a.permute('A'));

        Permutation r =
                new Permutation("(GRL) (HI) (FN)", new Alphabet("HILFNGR"));
        assertEquals('R', r.permute('G'));
        assertEquals('L', r.permute('R'));
        assertEquals('G', r.permute('L'));
        assertEquals('I', r.permute('H'));
        assertEquals('H', r.permute('I'));
        assertEquals('N', r.permute('F'));
        assertEquals('F', r.permute('N'));

    }

    @Test
    public void testInvertChar() {
        Permutation p = new Permutation("(BACD)", new Alphabet("(ABCD)"));
        assertEquals('D', p.invert('B'));
        assertEquals('B', p.invert('A'));
        assertEquals('A', p.invert('C'));
        assertEquals('C', p.invert('D'));

        Permutation a = new Permutation("(A)", new Alphabet("A"));
        assertEquals('A', a.invert('A'));

        Permutation r =
                new Permutation("(GRL) (HI) (FN)", new Alphabet("HILFNGR"));
        assertEquals('R', r.invert('L'));
        assertEquals('G', r.invert('R'));
        assertEquals('L', r.invert('G'));
        assertEquals('H', r.invert('I'));
        assertEquals('I', r.invert('H'));
        assertEquals('F', r.invert('N'));
        assertEquals('N', r.invert('F'));
    }

    @Test
    public void testPermuteInt() {
        Permutation a = new Permutation("(A)", new Alphabet("A"));
        assertEquals(0, a.permute(0));
    }

    @Test
    public void testInvertInt() {
        Permutation a = new Permutation("(A)", new Alphabet("A"));
        assertEquals(0, a.invert(0));
    }

    @Test
    public void testAlphabet() {
        Alphabet a = new Alphabet("BRUH");
        Permutation b = new Permutation("", a);
        assertEquals(a, b.alphabet());
    }

    @Test
    public void testDerangement() {
        Permutation a = new Permutation("(BC)", new Alphabet("BC"));
        assertTrue(a.derangement());
        Permutation a2 = new Permutation("(BA)", new Alphabet("ABC"));
        assertFalse(a2.derangement());
        Permutation f = new Permutation("(POMN)", new Alphabet("LMNOP"));
        assertFalse(f.derangement());
    }

}
