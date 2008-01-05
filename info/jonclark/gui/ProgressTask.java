package info.jonclark.gui;

public interface ProgressTask {

    /**
         * The number of events already recorded for this task
         */
    public void setProgress(int n);

    /**
         * The number of expected events for this task.
         */
    public void setMaximumValue(int n);

    /**
         * Did this task complete?
         */
    public void setDone(boolean b);

    /**
         * Informs the listener of how frequently it should expect updates
         * without declaring this task to be stalled.
         */
    public void setStallTimeout(int nSec);

    /**
         * Give the current operation a name.
         */
    public void setCurrentOperation(String operation);

    /**
         * When is this task expected to finish?
         */
    public void setEstimatedTimeRemaining(long time);

    /**
         * Set how much time has already been spent on this task.
         */
    public void setElapsedTime(long time);
}
