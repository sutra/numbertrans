package info.jonclark.corpus.management.iterators;

import java.io.File;

public interface ParallelCorpusTransformIterator extends CorpusIterator {

	public boolean hasNextPair();
	public void nextPair();
	
	public File getInputDocument(int nParallel);
	public File getOutputDocument(int nParallel);
}
