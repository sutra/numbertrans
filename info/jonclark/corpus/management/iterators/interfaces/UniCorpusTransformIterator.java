package info.jonclark.corpus.management.iterators.interfaces;

import info.jonclark.corpus.management.documents.InputDocument;
import info.jonclark.corpus.management.documents.OutputDocument;

import java.io.IOException;

public interface UniCorpusTransformIterator extends CorpusIterator {
    public boolean hasNext();

    public InputDocument getInputDocument() throws IOException;

    public OutputDocument getOutputDocument() throws IOException;
}
