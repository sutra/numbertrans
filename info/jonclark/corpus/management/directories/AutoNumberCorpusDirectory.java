package info.jonclark.corpus.management.directories;

import java.io.File;
import java.util.Properties;
import java.util.Vector;

/**
 * A directory type that takes the load off the file system and distributes
 * files into subdirectories such that only a maximum number of files will be
 * contained in each directory.
 * 
 */
public class AutoNumberCorpusDirectory extends AbstractCorpusDirectory {
	
	private final int nFilesPerDirectory;
	private int nCurrentDirectory = -1;
	private Vector<Integer> nChildFileCount = new Vector<Integer>();
	private Vector<AbstractCorpusDirectory> children = new Vector<AbstractCorpusDirectory>();

	public AutoNumberCorpusDirectory(Properties props, String namespace, File root) {
		super(props, namespace, root);
		// TODO Auto-generated constructor stub
		
		// init current files in directory
	}

	@Override
	public File[] getDocuments(DirectoryQuery query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getNextFileForCreation(DirectoryQuery query) {
		int nFilesInCurrentDirectory;
		
		if(nCurrentDirectory != -1) {
			nFilesInCurrentDirectory = nFilesPerDirectory;
		} else {
			nFilesInCurrentDirectory = nChildFileCount.get(nCurrentDirectory);
		}
		
		if(nFilesInCurrentDirectory >= nFilesPerDirectory) {
			// create new directory
		} else {
			// create new file in same directory
			return children.get(nCurrentDirectory).getNextFileForCreation(query));
		}
	}

}
