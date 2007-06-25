/*
 * Created on Jun 23, 2007
 */
package info.jonclark.corpus.management.iterators.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import info.jonclark.corpus.management.documents.OutputDocument;
import info.jonclark.corpus.management.iterators.interfaces.CorpusIterator;
import info.jonclark.lang.Pair;
import info.jonclark.log.LogUtils;
import info.jonclark.stat.RemainingTimeEstimator;
import info.jonclark.stat.SecondTimer;
import info.jonclark.util.ShutdownHook;
import info.jonclark.util.TimeLength;

public class AbstractIterator implements CorpusIterator {

    // sanity checking
    private static final Logger log = LogUtils.getLogger();
    protected ArrayList<Pair<OutputDocument, File>> currentOutputs = new ArrayList<Pair<OutputDocument, File>>(
	    3);

    // running counter for files
    protected int nFileIndex = -1;
    protected int nTotalFiles = 0;
    protected int nNextFileIndex = -1;

    // time monitoring
    private static final int EVENT_WINDOW = 500;
    protected final RemainingTimeEstimator est = new RemainingTimeEstimator(EVENT_WINDOW);
    protected final SecondTimer timer = new SecondTimer(false);

    public boolean validate() {
	boolean found = false;
	for (final Pair<OutputDocument, File> pair : currentOutputs)
	    if (!pair.first.isClosed())
		found = true;

	if (found) {
	    for (final Pair<OutputDocument, File> pair : currentOutputs) {
		log.warning("Unclosed file detected. Deleting file : "
			+ pair.second.getAbsolutePath());
		pair.first.close();
		pair.second.delete();
	    }
	}

	currentOutputs.clear();
	return found;
    }

    // do one last check for unclean files before the program exits
    @SuppressWarnings("unused")
    private ShutdownHook hook = new ShutdownHook() {
	@Override
	public void run() {
	    if(validate()) {
		log.info("Unclosed files detected and deleted.");
	    }
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

}
