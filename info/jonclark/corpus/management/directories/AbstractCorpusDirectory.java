package info.jonclark.corpus.management.directories;

import info.jonclark.corpus.management.etc.CorpusProperties;

import java.io.File;
import java.util.List;
import java.util.Properties;

public abstract class AbstractCorpusDirectory {

    private final String name;
    private final String type;
    private final AbstractCorpusDirectory child;

    public AbstractCorpusDirectory(Properties props, String namespace) {
	this.type = CorpusProperties.getType(props, namespace);
	this.name = CorpusProperties.getName(props, namespace);
	
	String subdir = CorpusProperties.getSubdirectory(props, namespace);
	this.child = CorpusDirectoryFactory.getCorpusDirectory(props, namespace + subdir);
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
     * Gets some statistic for the specified branches, as noted by the CorpusQuery
     * 
     * @param query
     * @param currentDirectory
     * @return
     */
    public abstract double getStatistic(CorpusQuery query, File currentDirectory);

    /**
         * Gets a list of files with constraints. The files may or may not
         * already exist.
         * 
         * @param query
         *                An object specifying which files should be returned.
         * @return
         */
    public abstract List<File> getDocuments(CorpusQuery query, File currentDirectory);
}
