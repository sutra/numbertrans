/*
 * Created on Jun 9, 2007
 */
package info.jonclark.corpus.management.iterators.impl;

import java.io.File;
import java.util.List;
import java.util.Properties;

import info.jonclark.corpus.management.directories.AbstractCorpusDirectory;
import info.jonclark.corpus.management.directories.CorpusDirectoryFactory;
import info.jonclark.corpus.management.directories.CorpusQuery;
import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.etc.InputDocument;
import info.jonclark.corpus.management.etc.OutputDocument;
import info.jonclark.corpus.management.etc.CorpusProperties;
import info.jonclark.corpus.management.iterators.interfaces.ParallelCorpusTransformIterator;

public class SimpleParallelTransformIterator implements ParallelCorpusTransformIterator {

    private final AbstractCorpusDirectory rootDirectory;
    private final File rootFile;
    private final String outputRunName;
    private final String inputRunName;

    private final List<File>[] parallelFiles;

    // running counter for parallel files
    private int nFileIndex = 0;
    private int nTotalFiles = 0;
    private String currentDocName;

    protected SimpleParallelTransformIterator(Properties props, String outputRunName)
	    throws CorpusManException {
	String corpusName = CorpusProperties.getCorpusNameFromRun(props, outputRunName);
	this.rootFile = CorpusProperties.getCorpusRootDirectoryFile(props, corpusName);
	this.rootDirectory = CorpusDirectoryFactory.getCorpusRootDirectory(props, corpusName);
	this.outputRunName = outputRunName;

	// later, we'll add queries for meta data
	CorpusQuery parallelQuery = new CorpusQuery(CorpusQuery.ALL_PARALLEL_DIRECTORIES,
		outputRunName, CorpusQuery.NO_FILE_NAME, CorpusQuery.NO_INDEX,
		CorpusQuery.Statistic.PARALLEL_COUNT);
	int nParallels = (int) rootDirectory.getStatistic(parallelQuery, rootFile);
	this.parallelFiles = createArray(nParallels);

	// do a little sanity checking to make sure we really have parallel
	// files
	int nPrevFiles = -1;
	for (int i = 1; i < nParallels; i++) {
	    CorpusQuery fileQuery = new CorpusQuery(i, outputRunName, CorpusQuery.ALL_FILES,
		    CorpusQuery.NO_INDEX, CorpusQuery.Statistic.DOCUMENT_COUNT);
	    int nFiles = (int) rootDirectory.getStatistic(fileQuery, rootFile);
	    if (nPrevFiles != -1 && nFiles != nPrevFiles) {
		throw new CorpusManException("Non-parallel directories detected. File count "
			+ (i - 1) + " = " + nPrevFiles + "; File count " + i + " = " + nFiles);
	    }

	    // now get all the files in this parallel
	    parallelFiles[i] = rootDirectory.getDocuments(fileQuery, rootFile);
	}

	// Get the input run
	this.inputRunName = CorpusProperties.getInputRunName(props, outputRunName);
    }

    /**
         * Limit our liability with warning supression
         */
    @SuppressWarnings("unchecked")
    private static List<File>[] createArray(int nSize) {
	return new List[nSize];
    }

    public InputDocument getInputDocument(int nParallel) {
	// TODO: Get one file at a time instead of just wailing on memory with
	// our array
	File file = parallelFiles[nParallel].get(nFileIndex);
	return new InputDocument(file);
    }

    public OutputDocument getOutputDocument(int nParallel) {
	CorpusQuery query = new CorpusQuery(nParallel, outputRunName, currentDocName, nFileIndex,
		CorpusQuery.Statistic.NONE);
	File file = rootDirectory.getNextFileForCreation(query, rootFile);
	return new OutputDocument(file);
    }

    public boolean hasNextPair() {
	return nFileIndex + 1 < nTotalFiles;
    }

    public void nextPair() {
	nFileIndex++;
    }

}
