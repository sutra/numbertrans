package info.jonclark.corpus.management.iterators;

import java.io.File;

public interface ParallelCorpusTransformIterator extends CorpusIterator {

	public boolean hasNextPair();
	public void nextPair();
	
	public File getInputDocumentA();
	public File getInputDocumentB();
	
	public File getOutputDocumentA();
	public File getOutputDocumentB();
}
