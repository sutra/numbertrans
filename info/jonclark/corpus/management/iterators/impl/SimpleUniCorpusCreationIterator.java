/*
 * Created on Jun 9, 2007
 */
package info.jonclark.corpus.management.iterators.impl;

import info.jonclark.corpus.management.directories.AbstractCorpusDirectory;
import info.jonclark.corpus.management.directories.CorpusDirectoryFactory;
import info.jonclark.corpus.management.directories.CorpusQuery;
import info.jonclark.corpus.management.directories.CorpusQuery.Statistic;
import info.jonclark.corpus.management.documents.MetaDocument;
import info.jonclark.corpus.management.documents.OutputDocument;
import info.jonclark.corpus.management.etc.BadFilenameException;
import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.etc.CorpusManRuntimeException;
import info.jonclark.corpus.management.etc.CorpusProperties;
import info.jonclark.corpus.management.etc.FileNamer;
import info.jonclark.corpus.management.iterators.interfaces.UniCorpusCreationIterator;
import info.jonclark.log.LogUtils;
import info.jonclark.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class SimpleUniCorpusCreationIterator extends AbstractIterator implements
	UniCorpusCreationIterator {

    private final AbstractCorpusDirectory rootDirectory;
    private final File rootFile;
    private final String outputRunName;
    private final FileNamer namer;
    private final boolean arrangeByFilename;

    private boolean checkedShouldSkip = false;

    // the parallel directory we're currdntly iterating through
    private final int nParallel;

    private static final Logger log = LogUtils.getLogger();

    /**
     * A constructor for use by the IteratorFactory <br>
     * If the namer is left null, we assume no autonaming is possible
     * 
     * @throws CorpusManException
     * @throws IOException
     */
    protected SimpleUniCorpusCreationIterator(Properties props, String outputRunName)
	    throws CorpusManException {
	super(props, outputRunName);

	String runNamespace = CorpusProperties.getRunNamespace(props, outputRunName);
	String corpusName = CorpusProperties.getCorpusNameFromRun(props, outputRunName);
	this.rootFile = CorpusProperties.getCorpusRootDirectoryFile(props, corpusName);
	this.rootDirectory = CorpusDirectoryFactory.getCorpusRootDirectory(props, corpusName);
	this.outputRunName = StringUtils.removeTrailingString(outputRunName, ".");
	this.arrangeByFilename = CorpusProperties.getArrangeByFilename(props, corpusName);

	if (CorpusProperties.hasInputEncoding(props, outputRunName))
	    throw new CorpusManException("Input encodings are not supported for Creation runs.");

	if (CorpusProperties.hasNodeFilenamePattern(props, outputRunName))
	    this.namer = new FileNamer(props, runNamespace);
	else
	    this.namer = null;

	String parallelDest = CorpusProperties.getUniCreateParallelDest(props, outputRunName);
	if (parallelDest != null) {
	    this.nParallel = CorpusProperties.getParallelIndex(props, corpusName, parallelDest);
	} else {
	    try {
		CorpusQuery query = new CorpusQuery(CorpusQuery.ALL_PARALLEL_DIRECTORIES,
			outputRunName, CorpusQuery.NO_FILE_NAME, CorpusQuery.NO_INDEX,
			Statistic.PARALLEL_COUNT);
		int nParallels = (int) rootDirectory.getStatistic(query, rootFile);
		if (nParallels == 0) {
		    this.nParallel = -1;
		} else {
		    throw new CorpusManException(
			    "For a parallel corpus, you must specify the parallelDest for a unicorpus run.");
		}
	    } catch (IOException e) {
		throw new CorpusManException(e);
	    }
	}
    }

    // private static int[] toParallelIndices(Properties props, String
    // corpusName,
    // String[] parallelDests) throws CorpusManException {
    // int[] parallelIndices = new int[parallelDests.length];
    // for (int i = 0; i < parallelDests.length; i++)
    // parallelIndices[i] = CorpusProperties.getParallelIndex(props,
    // corpusName,
    // parallelDests[i]);
    // return parallelIndices;
    // }

    /**
     * Actually creates the output file
     */
    public OutputDocument getOutputDocument() throws IOException {
	if (namer == null)
	    throw new CorpusManRuntimeException(
		    "You must specify a filename pattern use the auto-naming feature.");

	try {
	    return getOutputDocument(namer.getFilename(nFileIndex));
	} catch (BadFilenameException e) {
	    throw new CorpusManRuntimeException(e);
	}
    }

    public OutputDocument getOutputDocument(String docName) throws IOException,
	    BadFilenameException {
	if (nFileIndex == -1)
	    throw new CorpusManRuntimeException("You must call next() first.");
	if (!checkedShouldSkip)
	    log.warning("shouldSkip() was not checked.");

	CorpusQuery query = new CorpusQuery(nParallel, outputRunName, docName, nFileIndex,
		CorpusQuery.Statistic.NONE);
	if (arrangeByFilename)
	    query.fileIndex = namer.getIndexFromFilename(docName);

	File outputFile = rootDirectory.getNextFileForCreation(query, rootFile);
	createParent(outputFile);

	MetaDocument metadoc = super.getMetaFileFromOutputFile(outputFile, outputRunName);
	OutputDocument output = new OutputDocument(outputFile, metadoc, outputEncoding);
	super.addMonitorOutput(output, outputFile);
	return output;
    }

    /**
     * Moves to the next set of output documents, but does not actually create
     * any documents. Should be called AFTER each pair is added.
     */
    public void next() {
	super.nFileIndex++;
	checkedShouldSkip = false;

	super.updateStatus();

	boolean unclean = super.validate();
	if (unclean) {
	    throw new CorpusManRuntimeException("Unclosed documents detected and deleted.");
	}
    }

    public void setExpectedDocumentCount(int count) {
	super.nTotalFiles = count;
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

	CorpusQuery query = new CorpusQuery(nParallel, outputRunName, docName, nFileIndex,
		CorpusQuery.Statistic.NONE);
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
