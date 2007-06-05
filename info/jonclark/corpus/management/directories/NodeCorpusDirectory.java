package info.jonclark.corpus.management.directories;

import java.io.File;
import java.util.Properties;


public class NodeCorpusDirectory extends AbstractCorpusDirectory {

	public NodeCorpusDirectory(Properties props, String namespace, File root) {
		super(props, namespace, root);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public File[] getDocuments(DirectoryQuery query) {
		return this.getDirectoryFile().listFiles();
	}

	@Override
	public File getNextFileForCreation(DirectoryQuery query) {
		// TODO Auto-generated method stub
		
		// here we have to know:
		// 1. the pattern to generate filenames
		// 2. the global and local file count
		
		return null;
	}

}
