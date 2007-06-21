package info.jonclark.corpus.management.iterators.interfaces;

import info.jonclark.corpus.management.etc.OutputDocument;

public interface UniCorpusCreationIterator extends CorpusIterator {

    /**
         * Sets the expected number of documents so that the remaining time can
         * be estimated.
         * 
         * @param count
         *                The total number of documents that will be created
         */
    public void setExpectedDocumentCount(int count);

    public void next();

    public OutputDocument getOutputDocument();

    public OutputDocument getOutputDocument(String docName);
}
