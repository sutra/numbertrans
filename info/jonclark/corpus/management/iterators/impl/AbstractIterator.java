/*
 * Created on Jun 23, 2007
 */
package info.jonclark.corpus.management.iterators.impl;

import info.jonclark.corpus.management.directories.AbstractCorpusDirectory;
import info.jonclark.corpus.management.directories.CorpusDirectoryFactory;
import info.jonclark.corpus.management.documents.CloseableDocument;
import info.jonclark.corpus.management.documents.MetaDocument;
import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.etc.CorpusProperties;
import info.jonclark.corpus.management.iterators.interfaces.CorpusIterator;
import info.jonclark.lang.Pair;
import info.jonclark.log.LogUtils;
import info.jonclark.stat.RemainingTimeEstimator;
import info.jonclark.stat.SecondTimer;
import info.jonclark.util.FormatUtils;
import info.jonclark.util.ShutdownHook;
import info.jonclark.util.StringUtils;
import info.jonclark.util.TimeLength;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

public abstract class AbstractIterator implements CorpusIterator {

    // common elements
    protected final AbstractCorpusDirectory rootDirectory;
    protected final File rootFile;
    protected final String outputRunName;

    // sanity checking
    private static final Logger log = LogUtils.getLogger();
    private ArrayList<Pair<CloseableDocument, File>> currentOutputs = new ArrayList<Pair<CloseableDocument, File>>(
	    4);
    private Pair<CloseableDocument, File> currentRunOutput;
    private boolean finished = false;

    protected final Charset inputEncoding;
    protected final Charset outputEncoding;

    // running counter for files
    protected int nFileIndex = -1;
    protected int nTotalFiles = 0;
    protected int nNextFileIndex = -1;

    // status information
    private long lastUpdate = -1;
    private long updateInterval = 15 * 1000; // update every 15 sec

    // time monitoring
    private static final int EVENT_WINDOW = 500;
    protected final RemainingTimeEstimator est = new RemainingTimeEstimator(EVENT_WINDOW);
    protected final SecondTimer timer = new SecondTimer(false);

    public AbstractIterator(Properties props, String outputRunName) throws CorpusManException {
	this.inputEncoding = CorpusProperties.getInputEncoding(props, outputRunName);
	this.outputEncoding = CorpusProperties.getOutputEncoding(props, outputRunName);

	String corpusName = CorpusProperties.getCorpusNameFromRun(props, outputRunName);
	this.rootFile = CorpusProperties.getCorpusRootDirectoryFile(props, corpusName);
	this.rootDirectory = CorpusDirectoryFactory.getCorpusRootDirectory(props, corpusName);

	outputRunName = StringUtils.removeTrailingString(outputRunName, ".");
	this.outputRunName = outputRunName;
	
	timer.go();
    }

    protected MetaDocument getMetaFileFromInputFile(File inputFile, String inputRunName)
	    throws IOException {
	return getMetaFileFrom(inputFile, inputRunName);
    }

    protected MetaDocument getMetaFileFromOutputFile(File outputFile, String outputRunName)
	    throws IOException {
	return getMetaFileFrom(outputFile, outputRunName);
    }

    private MetaDocument getMetaFileFrom(File file, String runName) throws IOException {
	String metapath = StringUtils.replaceFast(file.getAbsolutePath(), "/" + runName + "/",
		"/meta/");
	File metafile = new File(metapath);
	createParent(metafile);

	MetaDocument metadoc = new MetaDocument(metafile);
	addMonitorOutput(metadoc, metafile);
	return metadoc;
    }

    protected static void createParent(File file) throws IOException {
	if (!file.getParentFile().exists())
	    if (!file.getParentFile().mkdirs())
		throw new IOException("Could not create parent directory for file: "
			+ file.getAbsolutePath());
    }
    
    protected void addMonitorOutput(CloseableDocument doc, File file) {
	Pair<CloseableDocument, File> pair = new Pair<CloseableDocument, File>(doc, file);
	currentOutputs.add(pair);
    }

    public boolean validate() {
	boolean found = false;
	for (final Pair<CloseableDocument, File> pair : currentOutputs)
	    if (!pair.first.isClosed())
		found = true;

	if (found) {
	    for (final Pair<CloseableDocument, File> pair : currentOutputs) {
		log.warning("Unclosed file detected. Deleting file : "
			+ pair.second.getAbsolutePath());
		try {
		    pair.first.close();
		    pair.second.delete();
		} catch (IOException e) {
		    log.warning("IO Error while attempting to close file: "
			    + pair.second.getAbsolutePath());
		}
	    }
	}
	currentOutputs.clear();
	
	return found;
    }

    private void validateRun() {
	if (validate()) {
	    log.info("Unclosed files detected and deleted.");
	}
	
	if(!finished) {
	    log.warning("finish() was not called at the end of this run!");
	    try {
		finish();
	    } catch (IOException e) {
		log.warning("IO Error while attempting to finish run.");
	    }
	}

	// now check the run metadata
	if (currentRunOutput != null && !currentRunOutput.first.isClosed()) {
	    log.warning("Unclosed run meta file detected. Attempting to close, but not deleting: "
		    + currentRunOutput.second.getAbsolutePath());
	    try {
		currentRunOutput.first.close();
	    } catch (IOException e) {
		log.warning("IO Error while attempting to close run meta file: "
			+ currentRunOutput.second.getAbsolutePath());
	    }
	}
	
	this.currentRunOutput = null;
    }

    // do one last check for unclean files before the program exits
    @SuppressWarnings("unused")
    private ShutdownHook hook = new ShutdownHook() {
	@Override
	public void run() {
	    validateRun();
	}
    };

    public TimeLength getElapsedTime() {
	return new TimeLength(timer.getMilliseconds());
    }

    public long getEstimatedCompletionTime() {
	int nRemaining = nTotalFiles - nFileIndex;
	return est.getEstimatedCompetionTime(nRemaining);
    }

    public TimeLength getRemainingTime() {
	int nRemaining = nTotalFiles - nFileIndex;
	return est.getRemainingTime(nRemaining);
    }

    /**
         * Synchronously updates the user about the status of this run with
         * information about estimated completion time, etc. IF the update
         * interval has expired
         */
    protected void updateStatus() {
	est.recordEvent();

	if (lastUpdate == -1) {
	    lastUpdate = System.currentTimeMillis();
	} else if (System.currentTimeMillis() - lastUpdate >= updateInterval) {
	    log.info("Current run: " + outputRunName + "\n" + "Elapsed time: "
		    + getElapsedTime().toStringMultipleUnits(2) + "\n" + " Est. Remaining time: "
		    + getRemainingTime().toStringMultipleUnits(2) + "\n"
		    + " Est. Time Completion: "
		    + FormatUtils.formatFullDate(new Date(getEstimatedCompletionTime())));

	    lastUpdate = System.currentTimeMillis();
	}
    }

    public MetaDocument getRunMetaData() {
	File metafile = new File(this.rootFile, outputRunName + ".meta");
	MetaDocument doc = new MetaDocument(metafile);
	this.currentRunOutput = new Pair<CloseableDocument, File>(doc, metafile);
	return doc;
    }

    public void finish() throws IOException {
	finished = true;
	
	MetaDocument meta = getRunMetaData();
	meta.set("elapsedTime", getElapsedTime().toStringAllUnits());
	meta.set("completionTime", FormatUtils.formatFullDate(new Date(System.currentTimeMillis())));
	meta.close();
	
	validateRun();
    }

    public abstract void next();
}
