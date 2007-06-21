/*
 * Created on Jun 9, 2007
 */
package info.jonclark.corpus.management.iterators.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import info.jonclark.corpus.management.directories.AbstractCorpusDirectory;
import info.jonclark.corpus.management.directories.CorpusDirectoryFactory;
import info.jonclark.corpus.management.directories.CorpusQuery;
import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.etc.InputDocument;
import info.jonclark.corpus.management.etc.OutputDocument;
import info.jonclark.corpus.management.etc.CorpusProperties;
import info.jonclark.corpus.management.iterators.interfaces.UniCorpusTransformIterator;
import info.jonclark.util.ArrayUtils;

public class SimpleUniTransformIterator implements UniCorpusTransformIterator {

    private final AbstractCorpusDirectory rootDirectory;
    private final File rootFile;
    private final String outputRunName;
    private final String inputRunName;

    private final List<File> allFiles = new ArrayList<File>(1000);

    // running counter for parallel files
    private int nFileIndex = 0;
    private int nTotalFiles = 0;
    private String currentDocName;

    // which parallel brach are we currently processing
    private String[] desiredParallelDirs;
    private int[] desiredParallelIndexes;
    private int currentParallelIndex;

    protected SimpleUniTransformIterator(Properties props, String outputRunName)
	    throws CorpusManException {
	String corpusName = CorpusProperties.getCorpusNameFromRun(props, outputRunName);
	this.rootFile = CorpusProperties.getCorpusRootDirectoryFile(props, corpusName);
	this.rootDirectory = CorpusDirectoryFactory.getCorpusRootDirectory(props, corpusName);
	this.outputRunName = outputRunName;

	// first get a list of all parallel dirs to check against
	String parallelNamespace = CorpusProperties.findParallelNamespace(props, corpusName);
	String[] allParallelDirs = CorpusProperties.getParallelTargets(props, parallelNamespace);

	// get the index of the desired parallel dirs
	this.desiredParallelDirs = CorpusProperties.getUniCorpusTransformParallelDirs(props,
		outputRunName);
	this.desiredParallelIndexes = new int[desiredParallelDirs.length];
	for (int i = 0; i < desiredParallelIndexes.length; i++)
	    desiredParallelIndexes[i] = ArrayUtils.findInUnsortedArray(allParallelDirs,
		    desiredParallelDirs[i]);
	this.currentParallelIndex = desiredParallelIndexes[0];

	// do a little sanity checking to make sure we really have parallel
	// files
	int nPrevFiles = -1;
	for (int i = 1; i < desiredParallelIndexes.length; i++) {
	    CorpusQuery fileQuery = new CorpusQuery(i, outputRunName, CorpusQuery.ALL_FILES,
		    CorpusQuery.NO_INDEX, CorpusQuery.Statistic.NONE);

	    // now get all the files in this parallel
	    allFiles.addAll(rootDirectory.getDocuments(fileQuery, rootFile));
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

    public InputDocument getInputDocument() {
	// TODO: Get one file at a time instead of just wailing on memory with
	// our array
	File file = allFiles.get(nFileIndex);
	return new InputDocument(file);
    }

    public OutputDocument getOutputDocument() {
	CorpusQuery query = new CorpusQuery(currentParallelIndex, outputRunName, currentDocName,
		nFileIndex, CorpusQuery.Statistic.NONE);
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
