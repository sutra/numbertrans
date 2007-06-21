package info.jonclark.corpus.management.directories;

import info.jonclark.corpus.management.directories.CorpusQuery.Statistic;
import info.jonclark.util.ArrayUtils;
import info.jonclark.util.FileUtils;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * A class to handle management files at the leaves of our directory tree.
 * <p>
 * NOTE: It is important that only one instance of this class exist for each
 * corpus.
 */
public class NodeCorpusDirectory extends AbstractCorpusDirectory {
    public NodeCorpusDirectory(Properties props, String namespace) {
	super(props, namespace);
    }

    @Override
    public List<File> getDocuments(CorpusQuery query, File currentDirectory) {
	return ArrayUtils.toArrayList(FileUtils.getNormalFiles(currentDirectory));
    }

    @Override
    public File getNextFileForCreation(CorpusQuery query, File currentDirectory) {
	File file = new File(currentDirectory, query.getFileName());
	return file;
    }

    @Override
    public double getStatistic(CorpusQuery query, File currentDirectory) {
	if (query.getStatistic() == Statistic.DOCUMENT_COUNT) {
	    
	    File[] files = FileUtils.getNormalFiles(currentDirectory);
	    return files.length;
	    
	} else if (query.getStatistic() == Statistic.PARALLEL_COUNT) {
	    return 0;
	} else if (query.getStatistic() == Statistic.NONE) {
	    return -1;
	} else {
	    throw new RuntimeException("Unknown Statistics: " + query.getStatistic());
	}
    }

}
