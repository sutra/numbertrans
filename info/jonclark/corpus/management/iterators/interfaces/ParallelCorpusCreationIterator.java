package info.jonclark.corpus.management.iterators.interfaces;

import info.jonclark.corpus.management.etc.OutputDocument;

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

    public void next();

    public OutputDocument getOutputDocument(int nParallel);

    public OutputDocument getOutputDocument(int nParallel, String docName);
}
