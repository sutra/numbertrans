/*
 * Created on Jun 9, 2007
 */
package info.jonclark.corpus.management.iterators.impl;

import info.jonclark.corpus.management.directories.AbstractCorpusDirectory;
import info.jonclark.corpus.management.directories.CorpusDirectoryFactory;
import info.jonclark.corpus.management.directories.CorpusQuery;
import info.jonclark.corpus.management.documents.OutputDocument;
import info.jonclark.corpus.management.etc.BadFilenameException;
import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.etc.CorpusManRuntimeException;
import info.jonclark.corpus.management.etc.CorpusProperties;
import info.jonclark.corpus.management.etc.FileNamer;
import info.jonclark.corpus.management.iterators.interfaces.ParallelCorpusCreationIterator;
import info.jonclark.lang.Pair;
import info.jonclark.log.LogUtils;
import info.jonclark.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class SimpleParallelCreationIterator extends AbstractIterator implements
	ParallelCorpusCreationIterator {

    private final AbstractCorpusDirectory rootDirectory;
    private final File rootFile;
    private final String outputRunName;
    private final FileNamer namer;

    private final int eParallel;
    private final int fParallel;

    private final boolean arrangeByFilename;
    private boolean checkedShouldSkip = false;

    private static final Logger log = LogUtils.getLogger();

    /**
         * A constructor for use by the IteratorFactory <br>
         * If the namer is left null, we assume no autonaming is possible
         * 
         * @throws CorpusManException
         */
    protected SimpleParallelCreationIterator(Properties props, String outputRunName)
	    throws CorpusManException {

	String runNamespace = CorpusProperties.getRunNamespace(props, outputRunName);
	String corpusName = CorpusProperties.getCorpusNameFromRun(props, outputRunName);
	this.rootFile = CorpusProperties.getCorpusRootDirectoryFile(props, corpusName);
	this.rootDirectory = CorpusDirectoryFactory.getCorpusRootDirectory(props, corpusName);
	this.arrangeByFilename = CorpusProperties.getArrangeByFilename(props, corpusName);

	outputRunName = StringUtils.removeTrailingString(outputRunName, ".");
	this.outputRunName = outputRunName;

	this.eParallel = CorpusProperties.getParallelIndexE(props, corpusName, outputRunName);
	this.fParallel = CorpusProperties.getParallelIndexF(props, corpusName, outputRunName);

	if (CorpusProperties.hasNodeFilenamePattern(props, outputRunName))
	    this.namer = new FileNamer(props, runNamespace);
	else
	    this.namer = null;
    }

    private OutputDocument getOutputDocument(int nParallel, String docName) throws IOException,
	    BadFilenameException {
	if (nFileIndex == -1)
	    throw new CorpusManRuntimeException("You must call next() first.");

	if (!checkedShouldSkip)
	    log.warning("shouldSkip() was not checked.");

	CorpusQuery query = new CorpusQuery(nParallel, outputRunName, docName, nFileIndex,
		CorpusQuery.Statistic.NONE);
	if (arrangeByFilename)
	    query.fileIndex = namer.getIndexFromFilename(docName);

	File file = rootDirectory.getNextFileForCreation(query, rootFile);

	if (!file.getParentFile().exists())
	    if (!file.getParentFile().mkdirs())
		throw new IOException("Could not create parent directory for file: "
			+ file.getAbsolutePath());

	OutputDocument output = new OutputDocument(file);
	currentOutputs.add(new Pair<OutputDocument, File>(output, file));
	return output;
    }

    public boolean hasNext() {
	return nFileIndex + 1 < nTotalFiles;
    }

    /**
         * Moves to the next set of output documents, but does not actually
         * create any documents. Should be called AFTER each pair is added.
         */
    public void next() {
	super.nFileIndex++;
	checkedShouldSkip = false;
	
	super.updateStatus();

	boolean unclean = validate();
	if (unclean) {
	    throw new CorpusManRuntimeException("Unclosed documents detected and deleted.");
	}
    }

    public void setExpectedDocumentCount(int count) {
	super.nTotalFiles = count;
    }

    public OutputDocument getOutputDocumentE() throws IOException {
	if (namer == null)
	    throw new CorpusManRuntimeException(
		    "You must specify a filename pattern use the auto-naming feature.");

	try {
	    return getOutputDocumentE(namer.getFilename(nFileIndex));
	} catch (BadFilenameException e) {
	    throw new CorpusManRuntimeException(e);
	}
    }

    public OutputDocument getOutputDocumentE(String docName) throws IOException,
	    BadFilenameException {
	return getOutputDocument(eParallel, docName);
    }

    public OutputDocument getOutputDocumentF() throws IOException {
	if (namer == null)
	    throw new CorpusManRuntimeException(
		    "You must specify a filename pattern use the auto-naming feature.");

	try {
	    return getOutputDocumentF(namer.getFilename(nFileIndex));
	} catch (BadFilenameException e) {
	    throw new CorpusManRuntimeException(e);
	}
    }

    public OutputDocument getOutputDocumentF(String docName) throws IOException,
	    BadFilenameException {
	return getOutputDocument(fParallel, docName);
    }

    public boolean shouldSkip() {
	try {
	    return shouldSkip(namer.getFilename(nFileIndex));
	} catch (BadFilenameException e) {
	    throw new CorpusManRuntimeException(e);
	}
    }

    public boolean shouldSkip(String docName) throws BadFilenameException {
	checkedShouldSkip = true;

	CorpusQuery query = new CorpusQuery(eParallel, outputRunName, docName, nFileIndex,
		CorpusQuery.Statistic.NONE);
	// don't update any counts for now
	query.simulate = true;
	if (arrangeByFilename)
	    query.fileIndex = namer.getIndexFromFilename(docName);

	File file = rootDirectory.getNextFileForCreation(query, rootFile);

	if (file.exists()) {
	    // we're going to skip this file, so go ahead and update
	    // counts in the directory structure
	    query.simulate = false;
	    rootDirectory.getNextFileForCreation(query, rootFile);
	    return true;
	} else {
	    return false;
	}
    }

}
