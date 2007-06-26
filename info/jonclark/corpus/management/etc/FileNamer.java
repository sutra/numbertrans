/*
 * Created on Jun 9, 2007
 */
package info.jonclark.corpus.management.etc;

import info.jonclark.util.StringUtils;

import java.text.DecimalFormat;
import java.util.Properties;

public class FileNamer {
    private final DecimalFormat format;
    private final String filePrefix;
    private final String fileSuffix;

    private final int nDiscardChars;

    public FileNamer(Properties props, String runNamespace) throws CorpusManException {
	String filenamePattern = CorpusProperties.getNodeFilenamePattern(props, runNamespace);
	String pattern = StringUtils.substringBetween(filenamePattern, "(", ")");

	this.format = new DecimalFormat(pattern);
	this.filePrefix = StringUtils.substringBefore(filenamePattern, "(");
	this.fileSuffix = StringUtils.substringAfter(filenamePattern, ")");

	this.nDiscardChars = filenamePattern.indexOf('(');
    }

    public String getFilename(int nFileIndex) {
	return filePrefix + format.format(nFileIndex) + fileSuffix;
    }

    /**
         * Used by the autonumber directory...
         * 
         * @throws CorpusManException
         */
    public int getIndexFromFilename(String filename) throws BadFilenameException {
	String remaining = filename.substring(nDiscardChars);
	int i;
	for (i = 0; i < remaining.length(); i++)
	    if (!Character.isDigit(remaining.charAt(i)))
		break;

	String number = remaining.substring(0, i);

	try {
	    int n = Integer.parseInt(number);
	    return n;
	} catch (NumberFormatException e) {
	    throw new BadFilenameException("Filename does not match expected pattern: " + filename, e);
	}
    }
}
