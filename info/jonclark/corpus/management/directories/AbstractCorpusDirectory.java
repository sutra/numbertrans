package info.jonclark.corpus.management.directories;

import java.io.File;
import java.util.Properties;

public abstract class AbstractCorpusDirectory {

	private final File root;

	public AbstractCorpusDirectory(Properties props, String namespace, File root) {
		this.root = root;
	}
	
	public File getDirectoryFile() {
		return root;
	}
	
	public abstract File getNextFileForCreation(DirectoryQuery query);
	
	/**
	 * Gets a list of files with constraints. The files may or may not already exist.
	 * 
	 * @param nParallel 0 or 1
	 * @param runName
	 * 
	 * @return
	 */
	public abstract File[] getDocuments(DirectoryQuery query);
}
