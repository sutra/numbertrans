/*
 * Created on Jun 9, 2007
 */
package info.jonclark.stat;

public class StringCombinationGenerator {
    private final IntegerCombinationGenerator gen;
    private final String[] strings;

    public StringCombinationGenerator(String[] strings, int nCombinationLength) {
	this.strings = strings;
	this.gen = new IntegerCombinationGenerator(strings.length, nCombinationLength);
    }

    public long getNumLeft() {
	return gen.getNumLeft();
    }

    public boolean hasMore() {
	return gen.hasMore();
    }

    /**
         * Get the total number of combinations
         */
    public long getTotal() {
	return gen.getTotal();
    }

    public String[] getNext() {
	int[] n = gen.getNext();
	String[] result = new String[n.length];
	for(int i=0; i<n.length; i++)
	    result[i] = strings[n[i]];
	return result;
    }
}
