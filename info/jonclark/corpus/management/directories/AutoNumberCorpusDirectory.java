package info.jonclark.corpus.management.directories;

import info.jonclark.corpus.management.directories.CorpusQuery.Statistic;
import info.jonclark.corpus.management.etc.CorpusProperties;
import info.jonclark.util.FileUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * A directory type that takes the load off the file system and distributes
 * files into subdirectories such that only a maximum number of files will be
 * contained in each directory.
 */
public class AutoNumberCorpusDirectory extends AbstractCorpusDirectory {

    private final boolean arrangeByFilename;
    private final int nFilesPerDirectory;
    private final DecimalFormat format;

    private int nCurrentSubdir = -1;
    private int nFilesInCurrentSubdir;
    private File currentSubdir;

    public AutoNumberCorpusDirectory(Properties props, String directoryNamespace) {
	super(props, directoryNamespace);

	this.nFilesPerDirectory = CorpusProperties.getAutoNumberFilesPerDir(props,
		directoryNamespace);
	// force creation of first directory
	this.nFilesInCurrentSubdir = this.nFilesPerDirectory;

	this.arrangeByFilename = CorpusProperties.getAutoNumberArrangeByFilename(props,
		directoryNamespace);

	String pattern = CorpusProperties.getAutoNumberPattern(props, directoryNamespace);
	this.format = new DecimalFormat(pattern);
    }

    @Override
    public List<File> getDocuments(CorpusQuery query, File currentDirectory) {

	File[] subdirs = FileUtils.getSubdirectories(currentDirectory);
	ArrayList<File> documents = new ArrayList<File>(nFilesPerDirectory * subdirs.length);
	for (final File subdir : subdirs)
	    documents.addAll(getChild().getDocuments(query, subdir));
	return documents;

    }

    @Override
    public double getStatistic(CorpusQuery query, File currentDirectory) {
	if (query.getStatistic() == Statistic.DOCUMENT_COUNT) {

	    File[] subdirs = FileUtils.getSubdirectories(currentDirectory);
	    double total = 0.0;
	    for (final File subdir : subdirs)
		total += getChild().getStatistic(query, subdir);
	    return total;

	} else if (query.getStatistic() == Statistic.PARALLEL_COUNT) {

	    File[] subdirs = FileUtils.getSubdirectories(currentDirectory);
	    return getChild().getStatistic(query, subdirs[0]);

	} else if (query.getStatistic() == Statistic.NONE) {
	    return -1;
	} else {
	    throw new RuntimeException("Unknown Statistics: " + query.getStatistic());
	}
    }

    @Override
    public File getNextFileForCreation(CorpusQuery query, File autonumberDirectory) {

	if (arrangeByFilename) {
	    // TODO: test me
	    // use query.fileNumber to implement this... maybe
	    // that idea probably needs to be rearchitected
	    throw new RuntimeException("Unimplemented");
	} else {
	    if (nFilesInCurrentSubdir >= nFilesPerDirectory) {
		// create new directory
		nFilesInCurrentSubdir = 0;
		nCurrentSubdir++;

		String directoryName = format.format(nCurrentSubdir);
		currentSubdir = new File(autonumberDirectory, directoryName);
	    }
	}

	nFilesInCurrentSubdir++;
	return getChild().getNextFileForCreation(query, currentSubdir);
    }
}
