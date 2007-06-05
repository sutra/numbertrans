package info.jonclark.corpus.management.iterators;

import java.io.File;


public interface UniCorpusCreationIterator extends CorpusIterator {
	public void nextPair();
	
	public File getOutputDocument();
}
