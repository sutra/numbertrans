package info.jonclark.corpus.management.iterators.interfaces;

import info.jonclark.corpus.management.documents.InputDocument;
import info.jonclark.corpus.management.documents.OutputDocument;

import java.io.IOException;

public interface ParallelCorpusAlignmentIterator extends CorpusIterator {

    public boolean hasNext();

    public InputDocument getInputDocumentE() throws IOException;
    
    public InputDocument getInputDocumentF() throws IOException;

    /**
         * A method to output multiple aligned files, if desired. (Usually the
         * format preferred as external program inputs).
         */
    public OutputDocument getOutputDocumentE() throws IOException;
    
    public OutputDocument getOutputDocumentF() throws IOException;

    /**
         * A method to output a singled aligned file, if desired. (Usually the
         * format preferred by humans doing debugging).
         * 
         * @throws IOException
         */
    public OutputDocument getAlignedOutputDocument() throws IOException;
}
