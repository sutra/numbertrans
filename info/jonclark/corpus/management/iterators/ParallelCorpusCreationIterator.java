package info.jonclark.corpus.management.iterators;

import java.io.File;


public interface ParallelCorpusCreationIterator extends CorpusIterator {
	public void nextPair();
	
	public File getOutputDocument(int nParallel);
}