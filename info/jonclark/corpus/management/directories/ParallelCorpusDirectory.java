package info.jonclark.corpus.management.directories;

import java.io.File;
import java.util.Properties;



public class ParallelCorpusDirectory extends AbstractCorpusDirectory {
	
	private final AbstractCorpusDirectory[] children = new AbstractCorpusDirectory[2];

	public ParallelCorpusDirectory(Properties props, String namespace, File root) {
		super(props, namespace, root);
		// TODO Auto-generated constructor stub
		
		// init children
	}

	@Override
	public File[] getDocuments(DirectoryQuery query) {
		return children[query.nParallel].getDocuments(query);
	}

	@Override
	public File getNextFileForCreation(DirectoryQuery query) {
		AbstractCorpusDirectory child = children[query.nParallel];
		
		File childFile = child.getDirectoryFile();
		if(!childFile.exists())
			childFile.mkdir();
		
		return child.getNextFileForCreation(query);
	}

}
