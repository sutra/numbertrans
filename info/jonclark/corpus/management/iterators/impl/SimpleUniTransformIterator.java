/*
 * Created on Jun 9, 2007
 */
package info.jonclark.corpus.management.iterators.impl;

import info.jonclark.corpus.management.directories.AbstractCorpusDirectory;
import info.jonclark.corpus.management.directories.CorpusDirectoryFactory;
import info.jonclark.corpus.management.directories.CorpusQuery;
import info.jonclark.corpus.management.documents.InputDocument;
import info.jonclark.corpus.management.documents.MetaDocument;
import info.jonclark.corpus.management.documents.OutputDocument;
import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.etc.CorpusManRuntimeException;
import info.jonclark.corpus.management.etc.CorpusProperties;
import info.jonclark.corpus.management.iterators.interfaces.UniCorpusTransformIterator;
import info.jonclark.util.ArrayUtils;
import info.jonclark.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SimpleUniTransformIterator extends AbstractIterator implements
	UniCorpusTransformIterator {

    private final AbstractCorpusDirectory rootDirectory;
    private final File rootFile;
    private final String outputRunName;
    private final String inputRunName;

    private final List<File> allFiles = new ArrayList<File>(1000);

    // running counter for parallel files
    private File currentInputFile;

    // which parallel brach are we currently processing
    private String[] desiredParallelDirs;
    private int[] desiredParallelIndexes;
    private int currentParallelIndex;

    private int foldSize;

    protected SimpleUniTransformIterator(Properties props, String outputRunName)
	    throws CorpusManException {
	super(props, outputRunName);

	String corpusName = CorpusProperties.getCorpusNameFromRun(props, outputRunName);
	this.rootFile = CorpusProperties.getCorpusRootDirectoryFile(props, corpusName);
	this.rootDirectory = CorpusDirectoryFactory.getCorpusRootDirectory(props, corpusName);

	outputRunName = StringUtils.removeTrailingString(outputRunName, ".");
	this.outputRunName = outputRunName;

	// first get a list of all parallel dirs to check against
	String parallelNamespace = CorpusProperties.findParallelNamespace(props, corpusName);
	String[] allParallelDirs = CorpusProperties.getParallelTargets(props, parallelNamespace);

	// get the index of the desired parallel dirs
	this.desiredParallelDirs = CorpusProperties.getUniCorpusTransformParallelDirs(props,
		outputRunName);
	this.desiredParallelIndexes = new int[desiredParallelDirs.length];
	for (int i = 0; i < desiredParallelIndexes.length; i++) {
	    desiredParallelIndexes[i] = ArrayUtils.findInUnsortedArray(allParallelDirs,
		    desiredParallelDirs[i]);
	}
	this.currentParallelIndex = desiredParallelIndexes[0];

	// Get the input run
	this.inputRunName = CorpusProperties.getInputRunName(props, outputRunName);

	// now we have the index of all desired parallel dirs

	try {
	    for (int i = 0; i < desiredParallelIndexes.length; i++) {
		CorpusQuery fileQuery = new CorpusQuery(desiredParallelIndexes[i], inputRunName,
			CorpusQuery.ALL_FILES, CorpusQuery.NO_INDEX, CorpusQuery.Statistic.NONE);

		// now get all the files in this parallel
		allFiles.addAll(rootDirectory.getDocuments(fileQuery, rootFile));
	    }
	    super.nTotalFiles = allFiles.size();
	    this.foldSize = super.nTotalFiles / desiredParallelIndexes.length;
	} catch (IOException e) {
	    throw new CorpusManException(e);
	}

	super.nNextFileIndex = findNext();
    }

    public InputDocument getInputDocument() throws IOException {
	if (nFileIndex == -1)
	    throw new CorpusManRuntimeException("You must call next() first.");

	// TODO: Get one file at a time instead of just wailing on memory with
	// our array
	
	MetaDocument metadoc = super.getMetaFileFromInputFile(currentInputFile, inputRunName);
	return new InputDocument(currentInputFile, metadoc, inputEncoding);
    }

    public OutputDocument getOutputDocument() throws IOException {
	if (nFileIndex == -1)
	    throw new CorpusManRuntimeException("You must call next() first.");

	CorpusQuery query = new CorpusQuery(currentParallelIndex, outputRunName,
		currentInputFile.getName(), nFileIndex, CorpusQuery.Statistic.NONE);
	File outputFile = rootDirectory.getNextFileForCreation(query, rootFile);
	createParent(outputFile);

	MetaDocument metadoc = super.getMetaFileFromOutputFile(outputFile, outputRunName);
	OutputDocument output = new OutputDocument(outputFile, metadoc, outputEncoding);
	super.addMonitorOutput(output, outputFile);
	return output;
    }

    private int findNext() {
	int i = super.nFileIndex + 1;
	while (i < allFiles.size()) {
	    File currentInputFile = allFiles.get(i);
	    CorpusQuery query = new CorpusQuery(currentParallelIndex, outputRunName,
		    currentInputFile.getName(), nFileIndex, CorpusQuery.Statistic.NONE);
	    File file = rootDirectory.getNextFileForCreation(query, rootFile);
	    if (!file.exists())
		return i;

	    i++;
	}

	return -1;
    }

    public boolean hasNext() {
	return (nNextFileIndex != -1);
    }

    public void next() {
	super.nFileIndex = super.nNextFileIndex;
	super.nNextFileIndex = findNext();
	this.currentInputFile = allFiles.get(nFileIndex);

	super.updateStatus();

	if (nFileIndex >= this.foldSize)
	    this.currentParallelIndex = this.desiredParallelIndexes[1];

	boolean unclean = validate();
	if (unclean) {
	    throw new CorpusManRuntimeException("Unclosed documents detected and deleted.");
	}
    }
}
