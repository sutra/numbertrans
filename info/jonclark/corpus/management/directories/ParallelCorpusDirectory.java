package info.jonclark.corpus.management.directories;

import info.jonclark.corpus.management.etc.CorpusGlobals;
import info.jonclark.corpus.management.etc.CorpusProperties;
import info.jonclark.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ParallelCorpusDirectory extends AbstractCorpusDirectory {
    private final String[] targets;

    public ParallelCorpusDirectory(Properties props, CorpusGlobals globals, String directoryNamespace) {
	super(props, globals, directoryNamespace);
	assert this.getType().equals("parallel") : "Incorrect directory type: " + this.getType();

	this.targets = CorpusProperties.getParallelTargets(props, directoryNamespace);
    }

    @Override
    public List<File> getDocuments(CorpusQuery query, File currentDirectory) {
	if (query.nParallel >= targets.length)
	    throw new RuntimeException("Invalid query for parallel directory: " + query.nParallel
		    + " when there are only " + targets.length + " parallel directories.");

	if (query.nParallel == CorpusQuery.ALL_PARALLEL_DIRECTORIES) {
	    File[] subdirs = FileUtils.getSubdirectories(currentDirectory);
	    ArrayList<File> documents = new ArrayList<File>(50000);
	    for(final File subdir : subdirs)
		documents.addAll(getChild().getDocuments(query, subdir));
	    return documents;
	} else {
	    File subdir = new File(currentDirectory, targets[query.nParallel]);
	    return getChild().getDocuments(query, subdir);
	}
    }

    @Override
    public File getNextFileForCreation(CorpusQuery query, File currentDirectory) {
	if (query.nParallel >= targets.length)
	    throw new RuntimeException("Invalid query for parallel directory: " + query.nParallel
		    + " when there are only " + targets.length + " parallel directories.");

	File childFile = new File(currentDirectory, targets[query.nParallel]);
	if (!childFile.exists())
	    childFile.mkdir();

	return getChild().getNextFileForCreation(query, childFile);
    }

}
