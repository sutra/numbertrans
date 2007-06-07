package info.jonclark.corpus.management.directories;

import info.jonclark.corpus.management.etc.CorpusProperties;

import java.io.File;
import java.util.Properties;


public class CorpusDirectoryFactory {
    
    public static AbstractCorpusDirectory getCorpusRootDirectory(Properties props, String corpusName) {
	String namespace = CorpusProperties.getCorpusNamespace(props, corpusName);
	File root = CorpusProperties.getCorpusRootDirectory(props, corpusName);
	return getCorpusDirectory(props, namespace, root);
    }
    
    public static AbstractCorpusDirectory getCorpusDirectory(Properties props, String namespace, File root) {
	String type = CorpusProperties.getType(props, namespace);
	if(type.equals("parallel")) {
	    return new ParallelCorpusDirectory(props, namespace, root);
	} else if(type.equals("autonumber")){
	    return new AutoNumberCorpusDirectory(props, namespace, root);
	} else if(type.equals("run")) {
	    return new RunCorpusDirectory(props, namespace, root);
	} else if(type.equals("node")) {
	    return new NodeCorpusDirectory(props, namespace, root);
	} else {
	    throw new RuntimeException("Unknown corpus directory type: " + type + " for directory " + namespace);
	}
    }
}
