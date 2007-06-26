/*
 * Created on Jun 21, 2007
 */
package info.jonclark.corpus.management.test;

import info.jonclark.corpus.management.documents.OutputDocument;
import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.iterators.interfaces.UniCorpusCreationIterator;
import info.jonclark.corpus.management.runs.UniCorpusCreationRun;

import java.util.Properties;

public class UniCreateTest implements UniCorpusCreationRun {

    public UniCreateTest(Properties props, String runName, String corpusName) {

    }

    public void processCorpus(UniCorpusCreationIterator iterator) throws CorpusManException {
	try {
	    for (int i = 0; i < 5; i++) {
		iterator.next();

		// show off arrangeByFilename
		if (i == 2)
		    continue;

		if (!iterator.shouldSkip()) {
		    OutputDocument out = iterator.getOutputDocument("page" + i + ".txt");
		    out.println(i + "");

		    // uncomment this line to see atomic file writing
		    // even when exceptions are generated
		    // throw new RuntimeException();

		    // comment out the close line to see unclosed file
                        // detection
		    out.close();
		} else {
		    System.out.println("Skipping existing file.");
		}
	    }
	} catch (Exception e) {
	    throw new CorpusManException(e);
	}
    }
}
