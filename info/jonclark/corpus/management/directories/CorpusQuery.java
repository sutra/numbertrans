package info.jonclark.corpus.management.directories;

public class CorpusQuery {
    public static final int ALL_PARALLEL_DIRECTORIES = -100;
    
    public int nParallel;
    public String runName;
    public int fileNumber;
    
    public CorpusQuery(int nParallel, String runName) {
	this(nParallel, runName, -1);
    }
    
    /**
     * 
     * @param nParallel
     * @param runName
     * @param fileNumber Optional parameter to force the fileNumber not to be the globalfile count
     */
    public CorpusQuery(int nParallel, String runName, int fileNumber) {
	this.nParallel = nParallel;
	this.runName = runName;
	this.fileNumber = fileNumber;
    }
}
