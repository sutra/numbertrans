/*
 * Created on Jun 25, 2007
 */
package info.jonclark.corpus.languagetools.ja;

import info.jonclark.corpus.management.documents.OutputDocument;
import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.iterators.interfaces.UniCorpusCreationIterator;
import info.jonclark.corpus.management.runs.UniCorpusCreationRun;
import info.jonclark.log.LogUtils;
import info.jonclark.util.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Move JPEN to the new corpus management system
 */
public class MoveJPEN implements UniCorpusCreationRun {

    private final String filter;
    private static final Logger log = LogUtils.getLogger();

    public MoveJPEN(Properties props, String runName, String corpusName) {
	if (runName.contains("EN")) {
	    this.filter = "F:/research/corpora/jpen.old/en/*/*.html";
	} else if (runName.contains("JA")) {
	    this.filter = "F:/research/corpora/jpen.old/jp/*/*.html";
	} else {
	    throw new RuntimeException("Unknown language.");
	}
    }

    public void processCorpus(UniCorpusCreationIterator iterator) throws CorpusManException {

	try {
	    log.info("Finding files for pattern: " + filter);
	    File[] files = FileUtils.getFilesFromWildcard(filter);

	    for (final File file : files) {
		iterator.next();
		if (iterator.shouldSkip()) {
		    System.out.print("o");
		} else {
		    System.out.print(".");

		    OutputDocument out = iterator.getOutputDocument();
		    BufferedReader in = new BufferedReader(new FileReader(file));

		    String line;
		    while ((line = in.readLine()) != null) {
			out.println(line);
		    }

		    in.close();
		    out.close();
		}
	    }
	} catch (IOException e) {
	    throw new CorpusManException(e);
	}
    }

}
