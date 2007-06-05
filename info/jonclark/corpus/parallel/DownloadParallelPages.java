/*
 * Created on May 24, 2007
 */
package info.jonclark.corpus.parallel;

import info.jonclark.stat.RemainingTimeEstimator;
import info.jonclark.util.FileUtils;
import info.jonclark.util.FormatUtils;
import info.jonclark.util.NetUtils;
import info.jonclark.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * Reads in a file containing URL pairs in STRANDS format and downloads them.
 * See http://www.umiacs.umd.edu/~resnik/strand/
 * 
 * @author Jonathan
 */
public class DownloadParallelPages {

    private final ArrayList<String> englishUrls = new ArrayList<String>();
    private final ArrayList<String> foreignUrls = new ArrayList<String>();

    public DownloadParallelPages(final String urlFile) throws IOException {
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

    public void downloadAllPages(final File englishDir, final File foreignDir) {

	englishDir.mkdirs();
	foreignDir.mkdirs();

	final RemainingTimeEstimator est = new RemainingTimeEstimator(500);

	int nGoodPageCount = 1;
	int nBadPageCount = 0;

	final int nFilesPerDirectory = 1000;
	int nDir = -nFilesPerDirectory;
	File currentEnglishDir = null;
	File currentForeignDir = null;
	
	for (int i = 1; i <= englishUrls.size(); i++) {
		
		if(i % nFilesPerDirectory == 0) {
			// put files in separate directories to appease the filesystem
			nDir += nFilesPerDirectory;
			String strDirName = StringUtils.forceNumberLength(nDir + "", 5);
			currentEnglishDir = new File(englishDir, strDirName);
			currentForeignDir = new File(englishDir, strDirName);
			currentEnglishDir.mkdir();
			currentForeignDir.mkdir();
		}
		
	    try {
		est.recordEvent();
		System.out.println("Downloading page pair " + i + " of " + englishUrls.size() + "("
			+ nGoodPageCount + " good pages and " + nBadPageCount
			+ " bad pages); estimated completion at "
			+ est.getEstimatedCompetionTimeFormatted(englishUrls.size() - i));

		final File englishFile = new File(currentEnglishDir, "page" + i + ".html");
		final InputStream englishStream = NetUtils.getUrlStream(englishUrls.get(i - 1));
		FileUtils.saveTextFileFromStream(englishFile, englishStream);

		final File foreignFile = new File(currentForeignDir, "page" + i + ".html");
		final InputStream foreignStream = NetUtils.getUrlStream(foreignUrls.get(i - 1));
		FileUtils.saveTextFileFromStream(foreignFile, foreignStream);

		nGoodPageCount++;
	    } catch (IOException e) {
		nBadPageCount++;
	    }
	}

	System.out.println("Download complete. (" + nGoodPageCount + " good pages and "
		+ nBadPageCount + " bad pages); time: " + FormatUtils.formatFullDate(new Date()));
    }

    public static void main(String... args) throws Exception {

	if (args.length != 3) {
	    System.err.println("Usage: program <url_file> <english_dir> <foreign_dir>");
	    System.exit(1);
	}
	
	final String urlFile = args[0];
	final File englishDir = new File(args[1]);
	final File foreignDir = new File(args[2]);
	final DownloadParallelPages down = new DownloadParallelPages(urlFile);
	down.downloadAllPages(englishDir, foreignDir);
    }
}
