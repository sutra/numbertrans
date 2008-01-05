/*
 * Created on Jun 21, 2007
 */
package info.jonclark.corpus.management.test;

import info.jonclark.corpus.management.documents.InputDocument;
import info.jonclark.corpus.management.documents.OutputDocument;
import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.iterators.interfaces.UniCorpusTransformIterator;
import info.jonclark.corpus.management.runs.UniCorpusTransformRun;
import info.jonclark.log.LogUtils;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class UniTransformTest implements UniCorpusTransformRun {

    private static final Logger log = LogUtils.getLogger();
    
    public UniTransformTest(Properties props, String runName, String corpusName) {

    }

    public void processCorpus(UniCorpusTransformIterator iterator) throws CorpusManException {
	try {
	    while (iterator.hasNext()) {
		iterator.next();
		
		System.out.print(".");
		
		InputDocument in = iterator.getInputDocument();
		OutputDocument out = iterator.getOutputDocument();
		
		String line;
		while ((line = in.readLine()) != null)
		    out.println(line);
		out.close();
	    }
	    
	    iterator.finish();
	} catch (IOException e) {
	    throw new CorpusManException(e);
	}
    }
}
