package info.jonclark.corpus.management.iterators.interfaces;

import java.io.IOException;

import info.jonclark.corpus.management.documents.OutputDocument;
import info.jonclark.corpus.management.etc.BadFilenameException;

public interface UniCorpusCreationIterator extends CorpusIterator {

    /**
         * Sets the expected number of documents so that the remaining time can
         * be estimated.
         * 
         * @param count
         *                The total number of documents that will be created
         */
    public void setExpectedDocumentCount(int count);

    /**
         * Returns true if we should skip the creation of output documents for
         * this input pair. The usual reason for this is if the output files
         * already exist.
         */
    public boolean shouldSkip();
    
    public boolean shouldSkip(String docName) throws BadFilenameException;

    /**
         * Must be called BEFORE the next document is read.
         */
    public void next();

    /**
         * Gets the next OutputDocument for creation IFF a naming pattern has
         * been specified for the run. If no naming pattern has been specified,
         * an exception is thrown.
         */
    public OutputDocument getOutputDocument() throws IOException;

    public OutputDocument getOutputDocument(String docName) throws IOException,
	    BadFilenameException;
}
