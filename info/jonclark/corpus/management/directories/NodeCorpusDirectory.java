package info.jonclark.corpus.management.directories;

import info.jonclark.corpus.management.etc.CorpusGlobals;
import info.jonclark.corpus.management.etc.CorpusProperties;
import info.jonclark.util.ArrayUtils;
import info.jonclark.util.FileUtils;
import info.jonclark.util.StringUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Properties;

/**
 * A class to handle management files at the leaves of our directory tree.
 * <p>
 * NOTE: It is important that only one instance of this class exist for each
 * corpus.
 */
public class NodeCorpusDirectory extends AbstractCorpusDirectory {

    private final String filePrefix;
    private final String fileSuffix;
    private final DecimalFormat format;

    public NodeCorpusDirectory(Properties props, CorpusGlobals globals, String namespace) {
	super(props, globals, namespace);

	String filenamePattern = CorpusProperties.getNodeFilenamePattern(props, namespace);
	String pattern = StringUtils.substringBetween(filenamePattern, "(", ")");
	this.format = new DecimalFormat(pattern);
	this.filePrefix = StringUtils.substringBefore(filenamePattern, "(");
	this.fileSuffix = StringUtils.substringAfter(filenamePattern, ")");
    }

    @Override
    public List<File> getDocuments(CorpusQuery query, File currentDirectory) {
	return ArrayUtils.toArrayList(FileUtils.getNormalFiles(currentDirectory));
    }

    @Override
    public File getNextFileForCreation(CorpusQuery query, File currentDirectory) {
	String filename = filePrefix + format.format(getGlobals().getGlobalFileCount())
		+ fileSuffix;
	
	getGlobals().incrementGlobalFileCount();

	File file = new File(currentDirectory, filename);
	return file;
    }

}
