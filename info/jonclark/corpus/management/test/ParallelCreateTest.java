/*
 * Created on Jun 21, 2007
 */
package info.jonclark.corpus.management.test;

import info.jonclark.corpus.management.documents.OutputDocument;
import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.iterators.interfaces.ParallelCorpusCreationIterator;
import info.jonclark.corpus.management.runs.ParallelCorpusCreationRun;

import java.util.Properties;

public class ParallelCreateTest implements ParallelCorpusCreationRun {

    public ParallelCreateTest(Properties props, String runName, String corpusName) {

    }

    public void processCorpus(ParallelCorpusCreationIterator iterator) throws CorpusManException {
	try {

	    for (int i = 0; i < 5; i++) {
		iterator.next();

		if (!iterator.shouldSkip()) {
		    OutputDocument e = iterator.getOutputDocumentE();
		    OutputDocument f = iterator.getOutputDocumentF();

		    e.println("E: " + i);
		    f.println("J: " + i);

		    e.close();
		    f.close();
		} else {
		    System.out.println("Skipping existing file.");
		}
		
		iterator.finish();
	    }
	} catch (Exception e) {
	    throw new CorpusManException(e);
	}
    }
}
