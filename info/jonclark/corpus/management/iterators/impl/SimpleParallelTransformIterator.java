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
import info.jonclark.corpus.management.iterators.interfaces.ParallelCorpusTransformIterator;
import info.jonclark.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class SimpleParallelTransformIterator extends AbstractIterator implements
	ParallelCorpusTransformIterator {

    protected final AbstractCorpusDirectory rootDirectory;
    protected final File rootFile;
    protected final String outputRunName;

    private final String inputRunNameE;
    private final String inputRunNameF;

    private final int eParallel;
    private final int fParallel;

    protected final List<File> eParallelFiles;
    protected final List<File> fParallelFiles;

    protected SimpleParallelTransformIterator(Properties props, String outputRunName)
	    throws CorpusManException {
	super(props, outputRunName);

	String corpusName = CorpusProperties.getCorpusNameFromRun(props, outputRunName);
	this.rootFile = CorpusProperties.getCorpusRootDirectoryFile(props, corpusName);
	this.rootDirectory = CorpusDirectoryFactory.getCorpusRootDirectory(props, corpusName);

	outputRunName = StringUtils.removeTrailingString(outputRunName, ".");
	this.outputRunName = outputRunName;

	// Get the input run
	this.inputRunNameE = CorpusProperties.getInputRunNameE(props, outputRunName);
	this.inputRunNameF = CorpusProperties.getInputRunNameF(props, outputRunName);

	this.eParallel = CorpusProperties.getParallelIndexE(props, corpusName, outputRunName);
	this.fParallel = CorpusProperties.getParallelIndexF(props, corpusName, outputRunName);

	try {
	    this.eParallelFiles = loadParallelFiles(props, corpusName, inputRunNameE, eParallel);
	    this.fParallelFiles = loadParallelFiles(props, corpusName, inputRunNameF, fParallel);
	    super.nNextFileIndex = findNext();

	} catch (IOException e) {
	    throw new CorpusManException(e);
	}
    }

    private List<File> loadParallelFiles(Properties props, String corpusName, String inputRunName,
	    int nParallel) throws IOException, CorpusManException {

	CorpusQuery fileQuery = new CorpusQuery(nParallel, inputRunName, CorpusQuery.ALL_FILES,
		CorpusQuery.NO_INDEX, CorpusQuery.Statistic.DOCUMENT_COUNT);
	List<File> parallelFiles = rootDirectory.getDocuments(fileQuery, rootFile);
	int nFiles = parallelFiles.size();

	if (super.nTotalFiles != 0 && super.nTotalFiles != nFiles) {
	    throw new CorpusManException("Non-parallel directories detected. File count "
		    + super.nTotalFiles + " != " + nFiles);
	}
	super.nTotalFiles = nFiles;

	return parallelFiles;
    }

    public InputDocument getInputDocumentE() throws IOException {
	return getInputDocument(eParallelFiles, inputRunNameE);
    }

    public InputDocument getInputDocumentF() throws IOException {
	return getInputDocument(fParallelFiles, inputRunNameF);
    }

    private InputDocument getInputDocument(List<File> parallelFiles, String inputRunName)
	    throws IOException {
	if (nFileIndex == -1)
	    throw new CorpusManRuntimeException("You must call next() first.");

	// TODO: Get one file at a time instead of just wailing on memory with
	// our array
	File inputFile = parallelFiles.get(nFileIndex);

	MetaDocument metadoc = super.getMetaFileFromInputFile(inputFile, inputRunName);
	return new InputDocument(inputFile, metadoc, inputEncoding);
    }

    public OutputDocument getOutputDocumentE() throws IOException {
	return getOutputDocument(eParallelFiles, eParallel);
    }

    public OutputDocument getOutputDocumentF() throws IOException {
	return getOutputDocument(fParallelFiles, fParallel);
    }

    protected OutputDocument getOutputDocument(List<File> parallelFiles, int nParallel)
	    throws IOException {
	if (nFileIndex == -1)
	    throw new CorpusManRuntimeException("You must call next() first.");

	File currentInputFile = parallelFiles.get(nFileIndex);
	CorpusQuery query = new CorpusQuery(nParallel, outputRunName, currentInputFile.getName(),
		nFileIndex, CorpusQuery.Statistic.NONE);
	File outputFile = rootDirectory.getNextFileForCreation(query, rootFile);
	createParent(outputFile);

	MetaDocument metadoc = super.getMetaFileFromOutputFile(outputFile, outputRunName);
	OutputDocument output = new OutputDocument(outputFile, metadoc, outputEncoding);
	super.addMonitorOutput(output, outputFile);
	return output;
    }

    private int findNext() {
	int i = super.nFileIndex + 1;
	while (i < eParallelFiles.size()) {

	    // we assume that if one output file exists, they both do
	    // WARNING: This assumption fails for the case of alignment
	    File currentInputFile = eParallelFiles.get(i);
	    CorpusQuery query = new CorpusQuery(eParallel, outputRunName,
		    currentInputFile.getName(), nFileIndex, CorpusQuery.Statistic.NONE);
	    query.simulate = true;
	    File file = rootDirectory.getNextFileForCreation(query, rootFile);
	    if (!file.exists()) {
		return i;
	    } else {
		// we're skipping this file, update counts
		query.simulate = false;
		rootDirectory.getNextFileForCreation(query, rootFile);
	    }

	    i++;
	}

	return -1;
    }

    public boolean hasNext() {
	return nNextFileIndex != -1;
    }

    public void next() {
	super.nFileIndex = super.nNextFileIndex;
	super.nNextFileIndex = findNext();

	super.updateStatus();

	boolean unclean = validate();
	if (unclean) {
	    throw new CorpusManRuntimeException("Unclosed documents detected and deleted.");
	}
    }

}
