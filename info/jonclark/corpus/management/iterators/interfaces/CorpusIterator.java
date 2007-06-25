package info.jonclark.corpus.management.iterators.interfaces;

import info.jonclark.util.TimeLength;

/**
 * Interface for all corpus iterators. Should never be implemented directly.
 * Instead choose one of its subinterfaces.
 */
public interface CorpusIterator {

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
}
