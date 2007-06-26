package info.jonclark.corpus.management.directories;

public class CorpusQuery {
    public static final int ALL_PARALLEL_DIRECTORIES = -100;
    public static final int NO_INDEX = -200;
    public static final String NO_FILE_NAME = "NO_FILE";
    public static final String ALL_FILES = "ALL_FILES";

    private final int nParallel;
    private final String runName;
    public int fileIndex;
    private final String fileName;
    
    /* Only simulate creating the file, don't update any counts */
    public boolean simulate = false;

    public enum Statistic {
	NONE, DOCUMENT_COUNT, PARALLEL_COUNT
    };
    
    private final Statistic stat;

    public CorpusQuery(int nParallel, String runName, String fileName, int fileIndex,
	    Statistic stat) {
	this.nParallel = nParallel;
	this.runName = runName;
	this.fileIndex = fileIndex;
	this.fileName = fileName;
	this.stat = stat;
    }

    public int getFileIndex() {
	return fileIndex;
    }

    public String getFileName() {
	return fileName;
    }

    public int getNParallel() {
	return nParallel;
    }

    public String getRunName() {
	return runName;
    }
    
    public Statistic getStatistic() {
	return stat;
    }
}
