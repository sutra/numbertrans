package info.jonclark.corpus.management.directories;

import info.jonclark.corpus.management.directories.CorpusQuery.Statistic;
import info.jonclark.util.FileUtils;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * A corpus directory that represents data at some given state (e.g. the
 * post-tokenization state) along the processing chain.
 * <p>
 * Also, the run type saves meta-data about when runs were performed.
 */
public class RunCorpusDirectory extends AbstractCorpusDirectory {
    
    public RunCorpusDirectory(Properties props, String namespace) {
	super(props, namespace);
    }

    @Override
    public List<File> getDocuments(CorpusQuery query, File currentDirectory) {
	
	File subdir = new File(currentDirectory, query.getRunName());
	return getChild().getDocuments(query, subdir);
	
    }

    @Override
    public File getNextFileForCreation(CorpusQuery query, File currentDirectory) {
	
	File subdir = new File(currentDirectory, query.getRunName());
	if(!subdir.exists())
	    subdir.mkdir();
	
	return getChild().getNextFileForCreation(query, subdir);
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

}
