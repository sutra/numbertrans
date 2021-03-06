package info.jonclark.corpus.management.iterators.interfaces;

import info.jonclark.corpus.management.documents.MetaDocument;
import info.jonclark.corpus.management.documents.OutputDocument;
import info.jonclark.corpus.management.etc.BadFilenameException;

import java.io.IOException;

public interface ParallelCorpusCreationIterator extends CorpusIterator {

    /**
         * Sets the expected number of documents so that the remaining time can
         * be estimated.
         * 
         * @param count
         *                The total number of parallel pairs that will be
         *                created
         */
    public void setExpectedDocumentCount(int count);

    /**
         * Returns true if we should skip the creation of output documents for
         * this input pair. The usual reason for this is if the output files
         * already exist.
         */
    public boolean shouldSkip();
    
    public boolean shouldSkip(String docName) throws BadFilenameException;

    public OutputDocument getOutputDocumentE() throws IOException;

    public OutputDocument getOutputDocumentE(String docName) throws IOException, BadFilenameException;

    public OutputDocument getOutputDocumentF() throws IOException;

    public OutputDocument getOutputDocumentF(String docName) throws IOException, BadFilenameException;
}
