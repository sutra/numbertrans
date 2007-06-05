/*
 * Created on May 27, 2007
 */
package info.jonclark.corpus.languagetools.ja;

import java.io.File;

import info.jonclark.util.FileUtils;
import info.jonclark.util.StringUtils;

public class FilterExpiredITMediaPages {
    public static void main(String... args) throws Exception {
	
	String jpFilter = "F:/research/corpora/jpen/jp/*/*.html";
	File[] jpFiles = FileUtils.getFilesFromWildcard(jpFilter);
	
	int i = 0;
	int nExpired = 0;
	for(final File jpFile : jpFiles) {
	    
	    final String html = FileUtils.getFileAsString(jpFile);
	    if(html.contains("<TITLE>Expired</TITLE>")) {
	    
	    final String strEnFile = StringUtils.replaceFast(jpFile.getAbsolutePath(), "jp", "en");
		final File enFile = new File(strEnFile);
		
		final String nameExpired = StringUtils.substringBefore(jpFile.getName(), ".html") + ".exp";
		final File enFileExpired = new File(enFile.getParentFile(), nameExpired);
		final File jpFileExpired = new File(jpFile.getParentFile(), nameExpired);
		
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
