package info.jonclark.corpus.management.runs;

import info.jonclark.corpus.management.iterators.interfaces.ParallelCorpusTransformIterator;

/**
 * Defines a class that performs some operator on the corpus. These classes
 * generally have a one-to-one mapping with runs.
 * 
 * @param <T>
 *                The iterator type which this run will use
 */
public interface ParallelCorpusTransformRun extends CorpusRun<ParallelCorpusTransformIterator> {}
