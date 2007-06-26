/*
 * Created on May 27, 2007
 */
package info.jonclark.corpus.languagetools.ja;

import info.jonclark.corpus.management.documents.InputDocument;
import info.jonclark.corpus.management.documents.OutputDocument;
import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.iterators.interfaces.UniCorpusTransformIterator;
import info.jonclark.corpus.management.runs.UniCorpusTransformRun;
import info.jonclark.util.FileUtils;
import info.jonclark.util.StringUtils;
import info.jonclark.log.LogUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class FilterExpiredITMediaPages implements UniCorpusTransformRun {

    private static final Logger log = LogUtils.getLogger();

    public FilterExpiredITMediaPages(Properties props, String runName, String corpusName) {

    }

    public void processCorpus(UniCorpusTransformIterator iterator) throws CorpusManException {
	int nExpired = 0;

	while (iterator.hasNext()) {
	    try {
		iterator.next();

		InputDocument in = iterator.getInputDocument();
		OutputDocument out = iterator.getOutputDocument();

		String html = in.getWholeFile();
		if (!html.contains("<TITLE>Expired</TITLE>")) {
		    out.copyFrom(in);
		} else {
		    nExpired++;
		}

		in.close();
		out.close();
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
