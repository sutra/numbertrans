package info.jonclark.corpus.management.directories;

import java.io.File;
import java.util.Properties;

/**
 * A corpus directory that represents data at some given state (e.g. the
 * post-tokenization state) along the processing chain.
 * <p>
 * Also, the run type saves meta-data about when runs were performed.
 */
public class RunCorpusDirectory extends AbstractCorpusDirectory {

	public RunCorpusDirectory(Properties props, String namespace, File root) {
		super(props, namespace, root);
		// TODO Auto-generated constructor stub
	}

	@Override
	public File[] getDocuments(DirectoryQuery query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getNextFileForCreation(DirectoryQuery query) {
		// TODO Auto-generated method stub
		return null;
	}

}
