/*
 * Created on Jun 9, 2007
 */
package info.jonclark.corpus.management.iterators.impl;

import java.io.File;

import info.jonclark.corpus.management.directories.AbstractCorpusDirectory;
import info.jonclark.corpus.management.directories.CorpusQuery;
import info.jonclark.corpus.management.etc.OutputDocument;
import info.jonclark.corpus.management.etc.FileNamer;
import info.jonclark.corpus.management.iterators.interfaces.ParallelCorpusCreationIterator;

public class SimpleParallelCreationIterator implements ParallelCorpusCreationIterator {

    private final AbstractCorpusDirectory rootDirectory;
    private final File rootFile;
    private final String runName;
    private final FileNamer namer;

    // running counter for parallel files
    private int nFileIndex = 0;
    private int nTotalFiles = 0;

    /**
         * A constructor for use by the IteratorFactory <br>
         * If the namer is left null, we assume no autonaming is possible
         */
    protected SimpleParallelCreationIterator(AbstractCorpusDirectory rootDirectory, File rootFile,
	    FileNamer namer, String runName) {
	this.rootDirectory = rootDirectory;
	this.rootFile = rootFile;
	this.runName = runName;
	this.namer = namer;
    }

    /**
         * Actually creates the output file
         */
    public OutputDocument getOutputDocument(int nParallel) {
	if (namer == null)
	    throw new RuntimeException(
		    "You must specify a filename pattern use the auto-naming feature.");

	return getOutputDocument(nParallel, namer.getFilename(nFileIndex));
    }

    public OutputDocument getOutputDocument(int nParallel, String docName) {
	CorpusQuery query = new CorpusQuery(nParallel, runName, docName, nFileIndex,
		CorpusQuery.Statistic.NONE);
	File file = rootDirectory.getNextFileForCreation(query, rootFile);
	return new OutputDocument(file);
    }
    
    public boolean hasNextPair() {
	return nFileIndex + 1 < nTotalFiles;
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
