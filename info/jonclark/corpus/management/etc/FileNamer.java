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

    public FileNamer(Properties props, String namespace) throws CorpusManException {
	String filenamePattern = CorpusProperties.getNodeFilenamePattern(props, namespace);
	String pattern = StringUtils.substringBetween(filenamePattern, "(", ")");

	this.format = new DecimalFormat(pattern);
	this.filePrefix = StringUtils.substringBefore(filenamePattern, "(");
	this.fileSuffix = StringUtils.substringAfter(filenamePattern, ")");
    }

    public String getFilename(int nFileIndex) {
	return filePrefix + format.format(nFileIndex) + fileSuffix;
    }

}
