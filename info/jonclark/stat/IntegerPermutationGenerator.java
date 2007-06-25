/*
 * Created on Jun 5, 2007
 */
package info.jonclark.stat;

public class IntegerPermutationGenerator {

    private int[] a;
    private long nPermutationsRemaining;
    private long nTotalPermutations;

    public IntegerPermutationGenerator(int n) {
	if (n < 1) {
	    throw new RuntimeException("n cannot be smaller than 1. Actual value: " + n);
	} else if(n > 20) {
	    throw new RuntimeException("n > 20 will produce a number too large to fit in a java long.");
	}
	a = new int[n];
	nTotalPermutations = computeFactorial(n);
	init();
    }

    private void init() {
	for (int i = 0; i < a.length; i++) {
	    a[i] = i;
	}
	nPermutationsRemaining = nTotalPermutations;
    }

    public long getPermutationsRemaining() {
	return nPermutationsRemaining;
    }

    public long getPermutationCount() {
	return nTotalPermutations;
    }

    public boolean hasNext() {
	return nPermutationsRemaining > 0;
    }

    public static long computeFactorial(int n) {
	long result = 1;
	for (int i = n; i > 1; i--)
	    result *= i;
	return result;
    }

    /**
         * Generate next permutation using an algorithm from Kenneth H. Rosen,
         * Discrete Mathematics and Its Applications, 2nd edition (NY:
         * McGraw-Hill, 1991), pp. 282-284
         */
    public int[] getNext() {

	if (nPermutationsRemaining == nTotalPermutations) {
	    nPermutationsRemaining--;
	    return a;
	}

	int temp;

	// Find largest index j with a[j] < a[j+1]
	int j = a.length - 2;
	while (a[j] > a[j + 1])
	    j--;

	// Find index k such that a[k] is smallest integer
	// greater than a[j] to the right of a[j]
	int k = a.length - 1;
	while (a[j] > a[k])
	    k--;

	// Interchange a[j] and a[k]
	temp = a[k];
	a[k] = a[j];
	a[j] = temp;

	// Put tail end of permutation after jth position in increasing order
	int r = a.length - 1;
	int s = j + 1;

	while (r > s) {
	    temp = a[s];
	    a[s] = a[r];
	    a[r] = temp;
	    r--;
	    s++;
	}

	nPermutationsRemaining--;
	return a;
    }
}
