/*
 * Created on Jun 6, 2007
 */
package info.jonclark.util;

public class StringPermutationGenerator {
    private final IntegerPermutationGenerator perm;
    private final String str;
    private int[] n;

    public StringPermutationGenerator(String str) {
	this.perm = new IntegerPermutationGenerator(str.length());
	this.str = str;
    }

    public boolean hasNext() {
	return perm.hasNext();
    }

    public long getPermutationCount() {
	return perm.getPermutationCount();
    }

    public String getNext() {
	n = perm.getNext();
	char[] chars = new char[str.length()];

	assert n.length == chars.length;
	assert n.length == str.length();

	for (int i = 0; i < chars.length; i++)
	    chars[i] = str.charAt(n[i]);

	return new String(chars);
    }

    /**
         * Gets the integer sequence that corresponds to the sequence of
         * characters from the last call to getNext()
         */
    public int[] getSequence() {
	return n;
    }

}
