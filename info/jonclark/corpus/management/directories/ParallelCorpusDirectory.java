package info.jonclark.corpus.management.directories;

import info.jonclark.corpus.management.etc.CorpusProperties;

import java.io.File;
import java.util.Properties;

public class ParallelCorpusDirectory extends AbstractCorpusDirectory {

    private final AbstractCorpusDirectory[] children;

    public ParallelCorpusDirectory(Properties props, String directoryNamespace, File root) {
	super(props, directoryNamespace, root);
	assert this.getType().equals("parallel") : "Incorrect directory type: " + this.getType();
	
	String[] targets = CorpusProperties.getParallelTargets(props, directoryNamespace);
	this.children = new AbstractCorpusDirectory[targets.length];
	for(int i=0; i<targets.length; i++) {
	    children[i] = CorpusDirectoryFactory.getCorpusDirectory(props, directoryNamespace + targets[i], new File(root, targets[i]));
	}
    }

    @Override
    public File[] getDocuments(DirectoryQuery query) {
	if (query.nParallel >= children.length)
	    throw new RuntimeException("Invalid query for parallel directory: " + query.nParallel
		    + " when there are only " + children.length + " parallel directories.");
	return children[query.nParallel].getDocuments(query);
    }

    @Override
    public File getNextFileForCreation(DirectoryQuery query) {
	if (query.nParallel >= children.length)
	    throw new RuntimeException("Invalid query for parallel directory: " + query.nParallel
		    + " when there are only " + children.length + " parallel directories.");

	AbstractCorpusDirectory child = children[query.nParallel];

	File childFile = child.getDirectoryFile();
	if (!childFile.exists())
	    childFile.mkdir();

	return child.getNextFileForCreation(query);
    }

}
