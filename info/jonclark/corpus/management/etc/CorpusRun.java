package info.jonclark.corpus.management.etc;

import java.util.Properties;

import info.jonclark.corpus.management.iterators.CorpusIterator;

/**
 * Defines a class that performs some operator on the corpus. These classes
 * generally have a one-to-one mapping with runs.
 * 
 * @param <T>
 *            The iterator type which this run will use
 */
public interface CorpusRun<T extends CorpusIterator> {

	public void processCorpus(Properties props, T iterator);
}
