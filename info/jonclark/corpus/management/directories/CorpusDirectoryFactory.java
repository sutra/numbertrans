package info.jonclark.corpus.management.directories;

import info.jonclark.corpus.management.etc.CorpusProperties;

import java.util.Properties;

public class CorpusDirectoryFactory {

    public static AbstractCorpusDirectory getCorpusRootDirectory(Properties props, String corpusName) {
	String namespace = CorpusProperties.getCorpusNamespace(props, corpusName);
	return getCorpusDirectory(props, namespace);
    }

    public static AbstractCorpusDirectory getCorpusDirectory(Properties props, String namespace) {
	String type = CorpusProperties.getType(props, namespace);
	if (type.equals("parallel")) {
	    return new ParallelCorpusDirectory(props, namespace);
	} else if (type.equals("autonumber")) {
	    return new AutoNumberCorpusDirectory(props, namespace);
	} else if (type.equals("run")) {
	    return new RunCorpusDirectory(props, namespace);
	} else if (type.equals("node")) {
	    return new NodeCorpusDirectory(props, namespace);
	} else {
	    throw new RuntimeException("Unknown corpus directory type: " + type + " for directory "
		    + namespace);
	}
    }
}
