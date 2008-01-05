package info.jonclark.corpus.management.iterators.interfaces;

import java.io.IOException;

import info.jonclark.corpus.management.documents.MetaDocument;
import info.jonclark.util.TimeLength;

/**
 * Interface for all corpus iterators. Should never be implemented directly.
 * Instead choose one of its subinterfaces.
 */
public interface CorpusIterator {

    /**
         * Must be called BEFORE the first document is read.
         */
    public void next();

    /**
         * Deletes any non-closed output files. This method is run by the
         * framework upon completion of a run or when the program exits. Use
         * with care.
         * 
         * @return True if any non-closed files were found, false otherwise.
         */
    public boolean validate();

    /**
         * Get the amount of time remaining for this run
         */
    public TimeLength getRemainingTime();

    /**
         * Get the estimated time of completetion for this run
         */
    public long getEstimatedCompletionTime();

    /**
         * Get the elapsed processing time in seconds
         */
    public TimeLength getElapsedTime();

    /**
         * Get the meta data local to this run, but global over all files in the
         * run.
         */
    public MetaDocument getRunMetaData();

    /**
         * Called at the end of each run to announce that the run was completed
         * successfully.
     * @throws IOException 
         */
    public void finish() throws IOException;
}
