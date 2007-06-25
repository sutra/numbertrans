/*
 * Created on Jun 9, 2007
 */
package info.jonclark.stat;

import java.util.ArrayList;
import java.util.HashMap;

import info.jonclark.util.ArrayUtils;

/**
 * Calculates F1 scores (and therefore precision and recall) for all possible
 * class subsets of a classifier.
 * <p>
 * For instance, if a test was run with 4 classes and the results don't depend
 * on the number of classes, then this class can give the F1 score for the cases
 * of having only 2 or 3 classes.
 * <p>
 * This class also averages the outcomes of all possible combinations of the
 * original classes given the new smaller class size.
 * <p>
 * TODO: Make this implementation more memory efficient.
 */
public class F1SubsetCalculator {

    private final F1Calculator orig;
    private final HashMap<Integer, F1Calculator[]> allCache = new HashMap<Integer, F1Calculator[]>();
    private final HashMap<Integer, F1Calculator> sumCache = new HashMap<Integer, F1Calculator>();

    public F1SubsetCalculator(F1Calculator original) {
	this.orig = original;
    }

    private void cacheCalculations(int nSubsetSize) {

	final int nOrigClasses = orig.getClassNames().length;
	IntegerCombinationGenerator gen = new IntegerCombinationGenerator(nOrigClasses, nSubsetSize);

	// allocate enough space for all combinations
	int nTotal = (int) gen.getTotal();
	F1Calculator[] calculations = new F1Calculator[nTotal];

	F1Calculator summary = new F1Calculator(orig.getClassNames());

	for (int iCombination = 0; iCombination < nTotal; iCombination++) {
	    int[] selected = gen.getNext();

	    String[] newClassNames = new String[nSubsetSize];
	    for (int iClass = 0; iClass < nSubsetSize; iClass++)
		newClassNames[iClass] = orig.getClassNames()[selected[iClass]];

	    F1Calculator calc = new F1Calculator(newClassNames);
	    ArrayList<double[]> origOutcomes = orig.getAllOutcomes();
	    ArrayList<Integer> origExpectedOutcomes = orig.getExpectedOutcomes();

	    for (int iOutcome = 0; iOutcome < origOutcomes.size(); iOutcome++) {
		final int nOrigExpectedClass = origExpectedOutcomes.get(iOutcome);
		final double[] origOutcome = origOutcomes.get(iOutcome);

		// if the expected class is not part of this subset,
		// ignore this outcome
		if (ArrayUtils.unsortedArrayContains(selected, nOrigExpectedClass)) {

		    int nSubsetExpectedClass = ArrayUtils.findInUnsortedArray(selected,
			    nOrigExpectedClass);
		    double[] subsetOutcome = new double[nSubsetSize];
		    for (int iClass = 0; iClass < nSubsetSize; iClass++)
			subsetOutcome[iClass] = origOutcome[selected[iClass]];
		    calc.addOutcome(subsetOutcome, nSubsetExpectedClass);

		    // now map this subset to the original and add to the
		    // summary
		    double[] mappedOutcome = new double[nOrigClasses];
		    for (int iClass = 0; iClass < nOrigClasses; iClass++) {
			if (ArrayUtils.unsortedArrayContains(selected, iClass))
			    mappedOutcome[iClass] = origOutcome[iClass];
		    }
		    summary.addOutcome(mappedOutcome, nOrigExpectedClass);
		}

	    }

	    calculations[iCombination] = calc;
	} // end iCombination

	allCache.put(nSubsetSize, calculations);
	sumCache.put(nSubsetSize, summary);
    }

    public F1Calculator[] getAllF1Calculations(int nSubsetSize) {
	if (!allCache.containsKey(nSubsetSize))
	    cacheCalculations(nSubsetSize);
	return allCache.get(nSubsetSize);
    }

    public F1Calculator getSummaryF1Calculation(int nSubsetSize) {
	if (!sumCache.containsKey(nSubsetSize))
	    cacheCalculations(nSubsetSize);
	return sumCache.get(nSubsetSize);
    }

    public String getFullF1Report(int nSubsetSize) {
	StringBuilder builder = new StringBuilder();
	builder.append(getSummaryF1Report(nSubsetSize));

	F1Calculator[] subsets = getAllF1Calculations(nSubsetSize);
	for (int i = 0; i < subsets.length; i++) {
	    builder.append("SUBSET " + (i + 1) + "/" + subsets.length + " - Subset size "
		    + nSubsetSize + "\n");
	    builder.append(subsets[i].getF1Report() + "\n");
	}

	return builder.toString();
    }

    public String getSummaryF1Report(int nSubsetSize) {
	StringBuilder builder = new StringBuilder();
	builder.append("SUMMARY - Subset size " + nSubsetSize + "\n");
	builder.append(getSummaryF1Calculation(nSubsetSize).getF1Report() + "\n");

	return builder.toString();
    }
}
