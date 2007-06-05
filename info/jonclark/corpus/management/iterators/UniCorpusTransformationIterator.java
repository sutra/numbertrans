package info.jonclark.corpus.management.iterators;

import java.io.File;


public interface UniCorpusTransformationIterator extends CorpusIterator {
	public boolean hasNextPair();
	public void nextPair();
	
	public File getInputDocument();
	
	public File getOutputDocument();
}
