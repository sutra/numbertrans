/*
 * Created on May 27, 2007
 */
package info.jonclark.corpus.parallel;

import java.io.File;

import info.jonclark.util.FileUtils;
import info.jonclark.util.StringUtils;

public class FilterExpiredITMediaPages {
    public static void main(String... args) throws Exception {
	
	File jpDir = new File("F:/research/corpora/jpen/jp");
	File enDir = new File("F:/research/corpora/jpen/en");
	File[] files = FileUtils.getFilesWithExt(jpDir, "");
	
	int i = 0;
	int nExpired = 0;
	for(final File jpFile : files) {
	    
	    final String html = FileUtils.getFileAsString(jpFile);
	    if(html.contains("<TITLE>Expired</TITLE>")) {
		final File enFile = new File(enDir, jpFile.getName());
		
		final String nameExpired = StringUtils.substringBefore(jpFile.getName(), ".html") + ".exp";
		final File enFileExpired = new File(enDir, nameExpired);
		final File jpFileExpired = new File(jpDir, nameExpired);
		
		jpFile.renameTo(jpFileExpired);
		enFile.renameTo(enFileExpired);
		
		System.out.println("Renamed expired page " + jpFile.getName());
		if(i % 1000 == 0)
		    System.out.println(i + " pages analyzed.");
		
		nExpired++;
		i++;
	    }
	}
	System.out.println("Done. " + nExpired + " expired pages");
    }
}
