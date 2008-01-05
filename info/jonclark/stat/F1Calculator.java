/*
 * Created on Jun 9, 2007
 */
package info.jonclark.stat;

import info.jonclark.util.ArrayUtils;

import java.util.ArrayList;

/**
 * Given a vector of outputs from a classifier system, calculats the F1 score
 * (and therefore the precision and recall as well).
 */
public class F1Calculator {

    private final String[] classNames;
    private final ArrayList<double[]> outcomes = new ArrayList<double[]>();
    private final ArrayList<Integer> expectedOutcomes = new ArrayList<Integer>();

    private final int[] timesClassFired;
    private final int[] timesClassFiredCorrectly;
    private final int[] timesClassExpected;

    // confidence values
    private double totalPercentDiffSum = 0.0;
    private final double[] classConfidenceSums;

    public F1Calculator(String[] classNames) {
	this.classNames = classNames;
	this.timesClassFired = new int[classNames.length];
	this.timesClassExpected = new int[classNames.length];
	this.timesClassFiredCorrectly = new int[classNames.length];
	this.classConfidenceSums = new double[classNames.length];
    }

    /**
         * Adds an outcome vector
         */
    public void addOutcome(double[] outcome, int nClassExpected) {
	outcomes.add(outcome);
	expectedOutcomes.add(nClassExpected);

	int nClassFired = ArrayUtils.indexOfMax(outcome);
	timesClassFired[nClassFired]++;
	timesClassExpected[nClassExpected]++;

	if (nClassFired == nClassExpected)
	    timesClassFiredCorrectly[nClassFired]++;

	// for an added bonus calculate a "confidence" score
	double max = outcome[ArrayUtils.indexOfMax(outcome)];
	double second = outcome[ArrayUtils.indexOfMax(outcome, 2)];
	double confidence = 2 * (max - second) / (max + second);

	totalPercentDiffSum += confidence;
	classConfidenceSums[nClassFired] += confidence;
    }
    
    public void addOutcome(int nClassFired, int nClassExpected) {
	double[] outcome = new double[classNames.length];
	outcome[nClassFired] = 1.0;
	addOutcome(outcome, nClassExpected);
    }

    protected ArrayList<Integer> getExpectedOutcomes() {
	return expectedOutcomes;
    }

    protected ArrayList<double[]> getAllOutcomes() {
	return outcomes;
    }

    public String[] getClassNames() {
	return classNames;
    }

    public int getTimesClassFired(int nClass) {
	return timesClassFired[nClass];
    }

    public int getTimesClassExpectedToFire(int nClass) {
	return timesClassExpected[nClass];
    }

    public int getTimesClassFiredCorrectly(int nClass) {
	return timesClassFiredCorrectly[nClass];
    }

    public String getPrecisionForClassAsFraction(int nClass) {
	return timesClassFiredCorrectly[nClass] + "/" + timesClassExpected[nClass];
    }

    public String getRecallForClassAsFraction(int nClass) {
	return timesClassFiredCorrectly[nClass] + "/" + timesClassFired[nClass];
    }

    public double getPrecisionForClass(int nClass) {
	double p = (double) timesClassFiredCorrectly[nClass] / (double) timesClassExpected[nClass];
	return p;
    }

    public double getRecallForClass(int nClass) {
	double r = (double) timesClassFiredCorrectly[nClass] / (double) timesClassFired[nClass];
	return r;
    }

    public double getF1ScoreForClass(int nClass) {
	double p = getPrecisionForClass(nClass);
	double r = getRecallForClass(nClass);
	double f1 = 2 * p * r / (p + r);
	return f1;
    }

    /**
         * Gets the percent difference between the weights for the top 2
         * classes, averaged over all times the class fired.
         * 
         * @param nClass
         * @return
         */
    public double getPercentDifferenceForClass(int nClass) {
	return classConfidenceSums[nClass] / (double) timesClassFired[nClass];
    }

    public String getTotalPrecisionAsFraction() {
	int nCorrect = 0;
	int nExpected = 0;
	for (int i = 0; i < classNames.length; i++) {
	    nCorrect += timesClassFiredCorrectly[i];
	    nExpected += timesClassExpected[i];
	}
	return nCorrect + "/" + nExpected;
    }

    public String getTotalRecallAsFraction() {
	int nCorrect = 0;
	int nFired = 0;
	for (int i = 0; i < classNames.length; i++) {
	    nCorrect += timesClassFiredCorrectly[i];
	    nFired += timesClassFired[i];
	}
	return nCorrect + "/" + nFired;
    }

    public double getTotalPrecision() {
	int nCorrect = 0;
	int nExpected = 0;
	for (int i = 0; i < classNames.length; i++) {
	    nCorrect += timesClassFiredCorrectly[i];
	    nExpected += timesClassExpected[i];
	}
	double p = (double) nCorrect / (double) nExpected;
	return p;
    }

    public double getTotalRecall() {
	int nCorrect = 0;
	int nFired = 0;
	for (int i = 0; i < classNames.length; i++) {
	    nCorrect += timesClassFiredCorrectly[i];
	    nFired += timesClassFired[i];
	}
	double r = (double) nCorrect / (double) nFired;
	return r;
    }

    public double getTotalF1Score() {
	double p = getTotalPrecision();
	double r = getTotalRecall();
	double f1 = 2 * p * r / (p + r);
	return f1;
    }

    public double getTotalPercentDifference() {
	return totalPercentDiffSum / (double) outcomes.size();
    }

    /**
         * Generates a full report in CSV format for later use by a program such
         * as excel.
         */
    public String getF1Report() {
	StringBuilder builder = new StringBuilder();
	builder.append("Class, F1 (%), Precision (%), Precision (correct/expected), Recall (%), Recall (correct/fired), Average %Diff (Top 2)\n");
	for (int i = 0; i < classNames.length; i++) {
	    builder.append(classNames[i] + ", " + getF1ScoreForClass(i) + ", "
		    + getPrecisionForClass(i) + ", " + getPrecisionForClassAsFraction(i) + ", "
		    + getRecallForClass(i) + ", " + getRecallForClassAsFraction(i) + ", "
		    + getPercentDifferenceForClass(i) + "\n");
	}
	builder.append("Total, " + getTotalF1Score() + ", " + getTotalPrecision() + ", "
		+ getTotalPrecisionAsFraction() + ", " + getTotalRecall() + ", "
		+ getTotalRecallAsFraction() + ", " + getTotalPercentDifference() + "\n");
	builder.append("\n");

	return builder.toString();
    }
}
