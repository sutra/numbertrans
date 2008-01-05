package info.jonclark.corpus.management.iterators.interfaces;

import info.jonclark.corpus.management.documents.InputDocument;
import info.jonclark.corpus.management.documents.OutputDocument;

import java.io.IOException;

public interface ParallelCorpusTransformIterator extends CorpusIterator {

    public boolean hasNext();

    public InputDocument getInputDocumentE() throws IOException;
    
    public InputDocument getInputDocumentF() throws IOException;

    public OutputDocument getOutputDocumentE() throws IOException;
    
    public OutputDocument getOutputDocumentF() throws IOException;
}
