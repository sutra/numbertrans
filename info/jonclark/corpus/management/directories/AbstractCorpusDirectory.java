package info.jonclark.corpus.management.directories;

import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.etc.CorpusProperties;
import info.jonclark.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public abstract class AbstractCorpusDirectory {

    private final String name;
    private final String type;
    private final AbstractCorpusDirectory child;

    public AbstractCorpusDirectory(Properties props, String namespace) throws CorpusManException {
	this.type = CorpusProperties.getCorpusDirectoryType(props, namespace);
	this.name = CorpusProperties.getDirectoryName(props, namespace);

	String subdir = CorpusProperties.getSubdirectory(props, namespace);

	// will only be null if this is a node directory
	if (subdir != null) {
	    String childNamespace = StringUtils.forceSuffix(namespace, ".") + subdir;
	    this.child = CorpusDirectoryFactory.getCorpusDirectory(props, childNamespace);
	} else {
	    this.child = null;
	}
    }

    /**
         * Gets the name the user assigned to this directory
         */
    public String getName() {
	return name;
    }

    /**
         * Gets the type of this directory. e.g. parallel, autonumber, etc.
         */
    public String getType() {
	return type;
    }

    protected AbstractCorpusDirectory getChild() {
	return child;
    }

    /**
         * Returns a new file for creation for use when initially creating a
         * corpus.
         * 
         * @param query
         *                An object specifying where the file should be created.
         * @return
         */
    public abstract File getNextFileForCreation(CorpusQuery query, File currentDirectory);

    /**
         * Gets some statistic for the specified branches, as noted by the
         * CorpusQuery
         * 
         * @param query
         * @param currentDirectory
         * @return
     * @throws IOException 
         */
    public abstract double getStatistic(CorpusQuery query, File currentDirectory)
	    throws IOException;

    /**
         * Gets a list of files with constraints. The files may or may not
         * already exist.
         * 
         * @param query
         *                An object specifying which files should be returned.
         * @return
     * @throws IOException 
         */
    public abstract List<File> getDocuments(CorpusQuery query, File currentDirectory)
	    throws IOException;
}
