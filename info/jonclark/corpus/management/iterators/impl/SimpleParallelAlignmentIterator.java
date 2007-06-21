/*
 * Created on Jun 9, 2007
 */
package info.jonclark.corpus.management.iterators.impl;

import java.util.Properties;

import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.etc.OutputDocument;
import info.jonclark.corpus.management.etc.CorpusProperties;
import info.jonclark.corpus.management.iterators.interfaces.ParallelCorpusAlignmentIterator;
import info.jonclark.util.ArrayUtils;

public class SimpleParallelAlignmentIterator extends SimpleParallelTransformIterator implements
	ParallelCorpusAlignmentIterator {

    private final int nParallelDest;

    protected SimpleParallelAlignmentIterator(Properties props, String outputRunName)
	    throws CorpusManException {
	super(props, outputRunName);

	String corpusName = CorpusProperties.getCorpusNameFromRun(props, outputRunName);

	// Find out which parallel directory the aligned files will be stored in
	String parallelDest = CorpusProperties.getParallelAlignmentDest(props, outputRunName);
	String parallelNamespace = CorpusProperties.findParallelNamespace(props, corpusName);
	String[] parallelTargets = CorpusProperties.getParallelTargets(props, parallelNamespace);
	this.nParallelDest = ArrayUtils.findInUnsortedArray(parallelTargets, parallelDest);
	if (nParallelDest == -1)
	    throw new CorpusManException("You must first create the parallel directory \""
		    + parallelDest + "\" before it can be used for output.");
    }

    public OutputDocument getAlignedOutputDocument() {
	return super.getOutputDocument(nParallelDest);
    }
}
