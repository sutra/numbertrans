package info.jonclark.corpus.management.iterators.interfaces;

import java.io.IOException;

import info.jonclark.corpus.management.documents.InputDocument;
import info.jonclark.corpus.management.documents.OutputDocument;

public interface ParallelCorpusTransformIterator extends CorpusIterator {

    public boolean hasNext();

    /**
         * Must be called BEFORE the next document is read.
         */
    public void next();

    public InputDocument getInputDocumentE() throws IOException;
    
    public InputDocument getInputDocumentF() throws IOException;

    public OutputDocument getOutputDocumentE() throws IOException;
    
    public OutputDocument getOutputDocumentF() throws IOException;
}
