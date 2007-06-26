/*
 * Created on May 24, 2007
 */
package info.jonclark.corpus.parallel;

import info.jonclark.corpus.management.documents.OutputDocument;
import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.iterators.interfaces.ParallelCorpusCreationIterator;
import info.jonclark.corpus.management.runs.ParallelCorpusCreationRun;
import info.jonclark.util.FormatUtils;
import info.jonclark.util.NetUtils;
import info.jonclark.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

/**
 * Reads in a file containing URL pairs in STRANDS format and downloads them.
 * See http://www.umiacs.umd.edu/~resnik/strand/
 * 
 * @author Jonathan
 */
public class DownloadParallelPages implements ParallelCorpusCreationRun {

    private final ArrayList<String> englishUrls = new ArrayList<String>();
    private final ArrayList<String> foreignUrls = new ArrayList<String>();

    public DownloadParallelPages(Properties props) throws IOException {
	String urlFile = props.getProperty("downloadParallelPages.urlFile");
	
	final BufferedReader in = new BufferedReader(new FileReader(urlFile));
	String line = in.readLine();
	while (line != null) {
	    final String[] tokens = StringUtils.tokenize(line, " \t");

	    if (tokens.length == 3) {
		foreignUrls.add(tokens[1]);
		englishUrls.add(tokens[2]);
	    }

	    line = in.readLine();
	}
    }

    public void processCorpus(ParallelCorpusCreationIterator iterator) throws CorpusManException {
	int nGoodPageCount = 0;
	int nBadPageCount = 0;
	
	for (int i = 1; i <= englishUrls.size(); i++) {
	    iterator.next();
	    try {
		System.out.println("Downloading page pair " + i + " of " + englishUrls.size() + "("
			+ nGoodPageCount + " good pages and " + nBadPageCount
			+ " bad pages)");

		String strE = NetUtils.getStreamAsString(NetUtils.getUrlStream(englishUrls.get(i - 1)));
		String strF = NetUtils.getStreamAsString(NetUtils.getUrlStream(foreignUrls.get(i - 1)));
		OutputDocument e = iterator.getOutputDocumentE();
		OutputDocument f = iterator.getOutputDocumentF();
		e.println(strE);
		f.println(strF);
		e.close();
		f.close();
		

		nGoodPageCount++;
		Thread.sleep(1000);
	    } catch (IOException e) {
		nBadPageCount++;
	    } catch (InterruptedException e) {
		;
	    }
	}

	System.out.println("Download complete. (" + nGoodPageCount + " good pages and "
		+ nBadPageCount + " bad pages); time: " + FormatUtils.formatFullDate(new Date()));
    }
}

