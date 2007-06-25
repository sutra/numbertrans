package info.jonclark.corpus.management.iterators.interfaces;

import java.io.IOException;

import info.jonclark.corpus.management.documents.InputDocument;
import info.jonclark.corpus.management.documents.OutputDocument;

public interface UniCorpusTransformIterator extends CorpusIterator {
    public boolean hasNext();

    /**
         * Must be called BEFORE the first document is read.
         */
    public void next();

    public InputDocument getInputDocument() throws IOException;

    public OutputDocument getOutputDocument() throws IOException;
}
