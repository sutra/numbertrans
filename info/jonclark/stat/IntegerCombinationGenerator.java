/*
 * Created on Jun 9, 2007
 */
package info.jonclark.stat;

/**
 * Generate combinations using the algorithm described by Kenneth H. Rosen,
 * Discrete Mathematics and Its Applications, 2nd edition (NY: McGraw-Hill,
 * 1991), pp. 284-286
 * <p>
 * Example Usage: <br>
 * <code>
 * String[] elements = {"a", "b", "c", "d", "e", "f", "g"};
 * int[] indices;
 * CombinationGenerator x = new CombinationGenerator (elements.length, 3);
 * StringBuffer combination;
 * while (x.hasMore ()) {
 * combination = new StringBuffer ();
 * indices = x.getNext ();
 * for (int i = 0; i < indices.length; i++) {
 *   combination.append (elements[indices[i]]);
 * }
 * System.out.println (combination.toString ());
 * }
 * </code>
 * 
 * @author Jonathan
 */
public class IntegerCombinationGenerator {

    private int[] a;
    private int n;
    private int r;
    private long numLeft;
    private long total;

    /**
     * TODO: Detection of input values that will overflow a long
     * 
     * @param n
     * @param r
     */
    public IntegerCombinationGenerator(int n, int r) {
	if (r > n) {
	    throw new IllegalArgumentException();
	}
	if (n < 1) {
	    throw new IllegalArgumentException();
	}
	this.n = n;
	this.r = r;
	a = new int[r];
	long nFact = getFactorial(n);
	long rFact = getFactorial(r);
	long nminusrFact = getFactorial(n - r);
	total = nFact / (rFact * nminusrFact);
	init();
    }

    private void init() {
	for (int i = 0; i < a.length; i++) {
	    a[i] = i;
	}
	numLeft = total;
    }

    public long getNumLeft() {
	return numLeft;
    }

    public boolean hasMore() {
	return (numLeft == 0);
    }

    /**
         * Get the total number of combinations
         */
    public long getTotal() {
	return total;
    }

    private static long getFactorial(int n) {
	long fact = 1;
	for (int i = n; i > 1; i--) {
	    fact = fact * i;
	}
	return fact;
    }

    public int[] getNext() {

	if (numLeft == total) {
	    numLeft--;
	    return a;
	}

	int i = r - 1;
	while (a[i] == n - r + i) {
	    i--;
	}
	a[i] = a[i] + 1;
	for (int j = i + 1; j < r; j++) {
	    a[j] = a[i] + j - i;
	}

	numLeft--;
	return a;

    }
}
