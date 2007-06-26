package info.jonclark.corpus.management.runs;

import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.iterators.interfaces.CorpusIterator;

/**
 * Defines a class that performs some operator on the corpus. These classes
 * generally have a one-to-one mapping with run definitions in properties files.
 * The user should never implement a CorpusRun directly, but instead choose one
 * of its subinterfaces.
 * <p>
 * The implementing class must define a constructor having three arguments of
 * types Properties (props), String (runName), String (corpusName)
 * 
 * @param <T>
 *                The iterator type which this run will use
 */
public abstract interface CorpusRun<T extends CorpusIterator> {

    /**
         * If any errors are encountered during processing, you should throw a
         * CorpusManException.
         * 
         * @param iterator
         * @throws CorpusManException
         */
    public abstract void processCorpus(T iterator) throws CorpusManException;
}
