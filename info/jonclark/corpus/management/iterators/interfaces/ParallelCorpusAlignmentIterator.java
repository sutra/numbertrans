package info.jonclark.corpus.management.iterators.interfaces;

import info.jonclark.corpus.management.etc.InputDocument;
import info.jonclark.corpus.management.etc.OutputDocument;

public interface ParallelCorpusAlignmentIterator extends CorpusIterator {

    public boolean hasNextPair();

    public void nextPair();

    public InputDocument getInputDocument(int nParallel);

    /**
         * A method to output multiple aligned files, if desired. (Usually the
         * format preferred as external program inputs).
         */
    public OutputDocument getOutputDocument(int nParallel);

    /**
         * A method to output a singled aligned file, if desired. (Usually the
         * format preferred by humans doing debugging).
         */
    public OutputDocument getAlignedOutputDocument();
}
