package info.jonclark.corpus.management.directories;

import info.jonclark.corpus.management.etc.CorpusGlobals;

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
    
    public RunCorpusDirectory(Properties props, CorpusGlobals globals, String namespace) {
	super(props, globals, namespace);
    }

    @Override
    public List<File> getDocuments(CorpusQuery query, File currentDirectory) {
	
	File subdir = new File(currentDirectory, query.runName);
	return getChild().getDocuments(query, subdir);
	
    }

    @Override
    public File getNextFileForCreation(CorpusQuery query, File currentDirectory) {
	
	File subdir = new File(currentDirectory, query.runName);
	if(!subdir.exists())
	    subdir.mkdir();
	
	return getChild().getNextFileForCreation(query, subdir);
    }

}
