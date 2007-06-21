/*
 * Created on Jun 9, 2007
 */
package info.jonclark.corpus.management.iterators.impl;

import java.io.File;
import java.util.Properties;

import info.jonclark.corpus.management.directories.AbstractCorpusDirectory;
import info.jonclark.corpus.management.directories.CorpusQuery;
import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.etc.CorpusProperties;
import info.jonclark.corpus.management.etc.OutputDocument;
import info.jonclark.corpus.management.etc.FileNamer;
import info.jonclark.corpus.management.iterators.interfaces.UniCorpusCreationIterator;

public class SimpleUniCorpusCreationIterator implements UniCorpusCreationIterator {

    private final AbstractCorpusDirectory rootDirectory;
    private final File rootFile;
    private final String runName;
    private final FileNamer namer;

    // running counter for parallel files
    private int nFileIndex = 0;

    // the parallel directory we're currdntly iterating through
    private final int nParallel;

    /**
         * A constructor for use by the IteratorFactory <br>
         * If the namer is left null, we assume no autonaming is possible
         * 
         * @throws CorpusManException
         */
    protected SimpleUniCorpusCreationIterator(Properties props,
	    AbstractCorpusDirectory rootDirectory, File rootFile, FileNamer namer, String runName)
	    throws CorpusManException {
	this.rootDirectory = rootDirectory;
	this.rootFile = rootFile;
	this.runName = runName;
	this.namer = namer;

	String corpusName = CorpusProperties.getCorpusNameFromRun(props, runName);
	String parallelDest = CorpusProperties.getUniCreateParallelDest(props, runName);
	if (parallelDest != null) {
	    nParallel = CorpusProperties.getParallelIndex(props, corpusName, parallelDest);
	} else {
	    // assume there's no parallel directory in this corpus
	    // TODO: Implement this in a much safer way
	    nParallel = -1;
	}
    }

    /**
         * Actually creates the output file
         */
    public OutputDocument getOutputDocument() {
	if (namer == null)
	    throw new RuntimeException(
		    "You must specify a filename pattern use the auto-naming feature.");

	return getOutputDocument(namer.getFilename(nFileIndex));
    }

    public OutputDocument getOutputDocument(String docName) {
	CorpusQuery query = new CorpusQuery(nParallel, runName, docName, nFileIndex,
		CorpusQuery.Statistic.NONE);
	File file = rootDirectory.getNextFileForCreation(query, rootFile);
	return new OutputDocument(file);
    }

    /**
         * Moves to the next set of output documents, but does not actually
         * create any documents. Should be called AFTER each pair is added.
         */
    public void next() {
	nFileIndex++;
    }

    public void setExpectedDocumentCount(int count) {

    }
}
