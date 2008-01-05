/*
 * Created on Jun 21, 2007
 */
package info.jonclark.corpus.management.test;

import info.jonclark.corpus.management.documents.InputDocument;
import info.jonclark.corpus.management.documents.OutputDocument;
import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.iterators.interfaces.ParallelCorpusAlignmentIterator;
import info.jonclark.corpus.management.runs.ParallelCorpusAlignmentRun;
import info.jonclark.log.LogUtils;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class ParallelAlignTest implements ParallelCorpusAlignmentRun {

    private static final Logger log = LogUtils.getLogger();

    public ParallelAlignTest(Properties props, String runName, String corpusName) {

    }

    public void processCorpus(ParallelCorpusAlignmentIterator iterator) throws CorpusManException {
	try {
	    while (iterator.hasNext()) {
		iterator.next();
		
		System.out.print(".");

		InputDocument ein = iterator.getInputDocumentE();
		OutputDocument eout = iterator.getOutputDocumentE();
		InputDocument jin = iterator.getInputDocumentF();
		OutputDocument jout = iterator.getOutputDocumentF();
		OutputDocument aout = iterator.getAlignedOutputDocument();

		String line;
		while ((line = ein.readLine()) != null) {
		    eout.println(line);
		    aout.println(line);
		}
		eout.close();

		while ((line = jin.readLine()) != null) {
		    jout.println(line);
		    aout.println(line);
		}
		jout.close();
		aout.close();
	    }
	    iterator.finish();
	} catch (IOException e) {
	    throw new CorpusManException(e);
	}
    }
}
