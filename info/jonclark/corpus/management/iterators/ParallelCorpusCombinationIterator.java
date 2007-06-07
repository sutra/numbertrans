package info.jonclark.corpus.management.iterators;

import java.io.File;


public interface ParallelCorpusCombinationIterator extends CorpusIterator {

	public boolean hasNextPair();
	public void nextPair();
	
	public File getInputDocument(int nParallel);
	public File getOutputDocument();
}
