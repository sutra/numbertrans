/*
 * Created on Jul 22, 2006
 */
package info.jonclark.testcases;

import java.util.Arrays;

import info.jonclark.lang.IntRange;
import info.jonclark.stat.SecondTimer;
import info.jonclark.util.StringUtils;
import info.jonclark.util.ArrayUtils;
import junit.framework.TestCase;

public class StringUtilsTest extends TestCase {
    private final SecondTimer timerFast = new SecondTimer(false);

    private final SecondTimer timerJava = new SecondTimer(false);

    private static final boolean useTime = false;

    public static void main(String[] args) {
	junit.textui.TestRunner.run(StringUtilsTest.class);
    }

    public void testReplaceFastIntRange() {
	assertEquals(StringUtils.replaceFast("aa aa aa aa",
		ArrayUtils.arrayToVector(new IntRange[] { new IntRange(3, 5) }),
		ArrayUtils.arrayToVector(new String[] { "bbb" })),
		"aa bbb aa aa");
	assertEquals(StringUtils.replaceFast("aa aa aa aa",
		ArrayUtils.arrayToVector(new IntRange[] { new IntRange(3, 5), new IntRange(7, 11)}),
		ArrayUtils.arrayToVector(new String[] { "bbb", "X"  })),
		"aa bbb aX");	
    }

    public void testReplaceFast() {
	assertEquals(StringUtils.replaceFast("aa aa aa aa", "aa", "bbb"), "bbb bbb bbb bbb");
	assertEquals(StringUtils.replaceFast("aa aa aa aa", "aa", "bb"), "bb bb bb bb");
	assertEquals(StringUtils.replaceFast("aa aa aa aa bbb ", "aa", "bbb"),
		"bbb bbb bbb bbb bbb ");
	assertEquals(StringUtils.replaceFast("abababab", "b", "a"), "aaaaaaaa");

	// Do some timed tests:
	if (useTime) {
	    timeReplaceFastCase("aa aa aa aa", "xx", "bb", "no replacement necessary");
	    timeReplaceFastCase("aa aa aa aa", "aa", "bb",
		    "find string and replacement string of same length");
	    timeReplaceFastCase("aa aa aa aa", "aa", "bbb",
		    "find string and replacement string of different length");
	}
    }

    public void timeReplaceFastCase(String target, String old, String replacement, String what) {
	final int nReplacements = 100000;

	timerFast.go();
	for (int i = 0; i < nReplacements; i++)
	    StringUtils.replaceFast(target, old, replacement);
	timerFast.pause();
	System.out.println("For " + what + ": ");
	System.out.println("\tFast: " + nReplacements + " in " + timerFast.getSeconds()
		+ " seconds");
	System.out
		.println("\tFast: " + timerFast.getEventsPerSecond(nReplacements) + " per second");

	timerJava.go();
	for (int i = 0; i < nReplacements; i++)
	    target.replace(old, replacement);
	timerJava.pause();
	System.out.println("\tJava: " + nReplacements + " in " + timerJava.getSeconds()
		+ " seconds");
	System.out
		.println("\tJava: " + timerJava.getEventsPerSecond(nReplacements) + " per second");
	System.out.println("\tSpeedup: "
		+ ((double) timerJava.getMilliseconds() / (double) timerFast.getMilliseconds()));

    }

    public void testCountOccurances() {
	assertEquals(StringUtils.countOccurancesOfSingleDelim("a a a a a", " "), 4);
	assertEquals(StringUtils.countOccurancesOfSingleDelim("abababab", "abab"), 2);
    }

    public void testSubstringAfter() {
	assertEquals(StringUtils.substringAfter("aabbccdd", "bb", false), "ccdd");
	assertEquals(StringUtils.substringAfter("aabbccdd", "bb", true), "bbccdd");
	assertEquals(StringUtils.substringAfter("aa", "aa", false), "");
    }

    public void testSubstringBefore() {
	assertEquals(StringUtils.substringBefore("aabbccdd", "bb", false), "aa");
	assertEquals(StringUtils.substringBefore("aabbccdd", "bb", true), "aabb");
	assertEquals(StringUtils.substringBefore("aabbccdd", "aa", false), "");
    }

    /*
         * Class under test for String[] tokenize(String)
         */
    public void testTokenizeString() {
	assertEquals(Arrays.equals(StringUtils.tokenize("a b c d"), new String[] { "a", "b", "c",
		"d" }), true);
    }

    /*
         * Class under test for String[] tokenize(String, String)
         */
    public void testTokenizeStringString() {
	assertEquals(Arrays.equals(StringUtils.tokenize("a.b.c.d", "."), new String[] { "a", "b",
		"c", "d" }), true);
    }

    public void testTokenizeStringStringString() {
	assertEquals(Arrays.equals(StringUtils.tokenize("a.b.c.d", ".", 3), new String[] { "a",
		"b", "c.d" }), true);
    }

    public void testUntokenize() {
	assertEquals(StringUtils.untokenize(new String[] { "a", "b", "c" }), "a b c");
    }

    /*
         * Class under test for String[] tokenizeIntern(String)
         */
    public void testTokenizeInternString() {
    }

    /*
         * Class under test for String[] tokenizeIntern(String, String)
         */
    public void testTokenizeInternStringString() {
    }

    public void testCutCharsFromEnd() {
	assertEquals(StringUtils.cutCharsFromEnd("abcd", 2), "ab");
    }
}
