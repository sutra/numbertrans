/*
 * Created on May 27, 2007
 */
package info.jonclark.corpus.languagetools.ja;

import info.jonclark.corpus.management.documents.InputDocument;
import info.jonclark.corpus.management.documents.OutputDocument;
import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.iterators.interfaces.ParallelCorpusTransformIterator;
import info.jonclark.corpus.management.runs.ParallelCorpusTransformRun;
import info.jonclark.log.LogUtils;
import info.jonclark.util.FileUtils;
import info.jonclark.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class FilterExpiredITMediaPages implements ParallelCorpusTransformRun {

    private static final Logger log = LogUtils.getLogger();

    public FilterExpiredITMediaPages(Properties props, String runName, String corpusName) {

    }

    public void processCorpus(ParallelCorpusTransformIterator iterator) throws CorpusManException {
	int nExpired = 0;

	while (iterator.hasNext()) {
	    try {
		iterator.next();

		InputDocument inE = iterator.getInputDocumentE();
		InputDocument inF = iterator.getInputDocumentF();
		OutputDocument outE = iterator.getOutputDocumentE();
		OutputDocument outF = iterator.getOutputDocumentF();

		String html = inF.getWholeFile();
		if (!html.contains("<TITLE>Expired</TITLE>")) {
		    outE.copyFrom(inE);
		    outF.copyFrom(inF);
		} else {
		    nExpired++;
		}
		
		iterator.finish();
	    } catch (IOException e) {
		throw new CorpusManException(e);
	    }
	}

	log.info("Done. " + nExpired + " expired pages");
    }

    public static void main(String... args) throws Exception {

	String jpFilter = "F:/research/corpora/jpen/jp/*/*.html";
	File[] jpFiles = FileUtils.getFilesFromWildcard(jpFilter);

	int i = 0;
	int nExpired = 0;
	for (final File jpFile : jpFiles) {

	    final String html = FileUtils.getFileAsString(jpFile);
	    if (html.contains("<TITLE>Expired</TITLE>")) {

		final String strEnFile = StringUtils.replaceFast(jpFile.getAbsolutePath(), "jp",
			"en");
		final File enFile = new File(strEnFile);

		final String nameExpired = StringUtils.substringBefore(jpFile.getName(), ".html")
			+ ".exp";
		final File enFileExpired = new File(enFile.getParentFile(), nameExpired);
		final File jpFileExpired = new File(jpFile.getParentFile(), nameExpired);

		jpFile.renameTo(jpFileExpired);
		enFile.renameTo(enFileExpired);

		System.out.println("Renamed expired page " + jpFile.getName());
		if (i % 1000 == 0)
		    System.out.println(i + " pages analyzed.");

		nExpired++;
		i++;
	    }
	}
	System.out.println("Done. " + nExpired + " expired pages");
    }
}

