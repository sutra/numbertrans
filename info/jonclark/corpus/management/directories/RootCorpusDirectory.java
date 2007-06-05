package info.jonclark.corpus.management.directories;

import info.jonclark.corpus.management.etc.CorpusProperties;
import info.jonclark.properties.PropertiesException;

import java.util.Properties;

public class RootCorpusDirectory extends AbstractCorpusDirectory {

	public RootCorpusDirectory(Properties props, String corpusName) throws PropertiesException {
		
		super(props, CorpusProperties.getCorpusNamespace(props, corpusName), CorpusProperties
				.getCorpusRootDirectory(props, corpusName), false);
		
		
	}
}
