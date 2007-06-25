package info.jonclark.corpus.management.runs;

import info.jonclark.corpus.management.iterators.interfaces.ParallelCorpusAlignmentIterator;

/**
 * Defines a class that performs some operator on the corpus. These classes
 * generally have a one-to-one mapping with runs.
 * 
 * @param <T>
 *                The iterator type which this run will use
 */
public interface ParallelCorpusAlignmentRun extends CorpusRun<ParallelCorpusAlignmentIterator> {}
