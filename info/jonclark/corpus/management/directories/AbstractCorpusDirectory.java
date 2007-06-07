package info.jonclark.corpus.management.directories;

import info.jonclark.corpus.management.etc.CorpusProperties;

import java.io.File;
import java.util.Properties;

public abstract class AbstractCorpusDirectory {

    private final File root;
    private final String name;
    private final String type;

    public AbstractCorpusDirectory(Properties props, String namespace, File root) {
	this.root = root;
	this.type = CorpusProperties.getType(props, namespace);
	this.name = CorpusProperties.getName(props, namespace);
    }

    /**
         * Gets the file that represents the path to this directory.
         * 
         * @return
         */
    public File getDirectoryFile() {
	return root;
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

    /**
         * Returns a new file for creation for use when initially creating a
         * corpus.
         * 
         * @param query
         *                An object specifying where the file should be created.
         * @return
         */
    public abstract File getNextFileForCreation(DirectoryQuery query);

    /**
         * Gets a list of files with constraints. The files may or may not
         * already exist.
         * 
         * @param query
         *                An object specifying which files should be returned.
         * @return
         */
    public abstract File[] getDocuments(DirectoryQuery query);
}
