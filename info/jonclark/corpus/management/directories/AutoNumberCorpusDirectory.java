package info.jonclark.corpus.management.directories;

import info.jonclark.corpus.management.directories.CorpusQuery.Statistic;
import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.etc.CorpusProperties;
import info.jonclark.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
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

    private final HashMap<File, AutoNumberData> dirs = new HashMap<File, AutoNumberData>();

    private class AutoNumberData implements Cloneable {
	public int nCurrentSubdir = -1;
	public int nFilesInCurrentSubdir = Integer.MAX_VALUE; // force
	// creation
	public File currentSubdir = null;

	public AutoNumberData clone() {
	    try {
		return (AutoNumberData) super.clone();
	    } catch (CloneNotSupportedException e) {
		throw new Error(e);
	    }
	}
    }

    public AutoNumberCorpusDirectory(Properties props, String directoryNamespace)
	    throws CorpusManException {
	super(props, directoryNamespace);

	this.nFilesPerDirectory = CorpusProperties.getAutoNumberFilesPerDir(props,
		directoryNamespace);
	;

	this.arrangeByFilename = CorpusProperties.getAutoNumberArrangeByFilename(props,
		directoryNamespace);

	String pattern = CorpusProperties.getAutoNumberPattern(props, directoryNamespace);
	this.format = new DecimalFormat(pattern);
    }

    @Override
    public List<File> getDocuments(CorpusQuery query, File currentDirectory) throws IOException {

	File[] subdirs = FileUtils.getSubdirectories(currentDirectory);
	ArrayList<File> documents = new ArrayList<File>(nFilesPerDirectory * subdirs.length);
	for (final File subdir : subdirs)
	    documents.addAll(getChild().getDocuments(query, subdir));
	return documents;

    }

    @Override
    public double getStatistic(CorpusQuery query, File currentDirectory) throws IOException {
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

	AutoNumberData data = dirs.get(autonumberDirectory);
	if (data == null) {
	    // create a new data object with an "infinite" number of files
	    // which forces the creation of the first directory
	    data = new AutoNumberData();
	    if (!query.simulate)
		dirs.put(autonumberDirectory, data);
	    
	} else if (query.simulate) {
	    // don't mess up our real data if we're just simulating
	    data = data.clone();
	}

	if (arrangeByFilename) {
	    // TODO: test me
	    // use query.fileNumber to implement this... maybe
	    // that idea probably needs to be rearchitected
	    throw new RuntimeException("Unimplemented");
	} else {
	    if (data.nFilesInCurrentSubdir >= nFilesPerDirectory) {
		// create new directory
		data.nFilesInCurrentSubdir = 0;
		data.nCurrentSubdir++;

		String directoryName = format.format(data.nCurrentSubdir);
		data.currentSubdir = new File(autonumberDirectory, directoryName);
	    }
	}

	data.nFilesInCurrentSubdir++;
	return getChild().getNextFileForCreation(query, data.currentSubdir);
    }
}
