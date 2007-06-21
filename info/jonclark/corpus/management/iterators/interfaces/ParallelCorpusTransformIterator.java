package info.jonclark.corpus.management.iterators.interfaces;

import info.jonclark.corpus.management.etc.InputDocument;
import info.jonclark.corpus.management.etc.OutputDocument;

public interface ParallelCorpusTransformIterator extends CorpusIterator {

	public boolean hasNextPair();
	public void nextPair();
	
	public InputDocument getInputDocument(int nParallel);
	public OutputDocument getOutputDocument(int nParallel);
}
