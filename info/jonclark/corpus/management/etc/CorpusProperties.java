package info.jonclark.corpus.management.etc;

import java.io.File;
import java.util.Properties;

import info.jonclark.properties.PropertiesException;
import info.jonclark.properties.PropertyUtils;
import info.jonclark.util.StringUtils;

public class CorpusProperties {

	public static String getRunNamespace(Properties props, String corpusName, String runName)
			throws PropertiesException {

		String allRunsNamespace = "run." + corpusName;
		String nameKey = PropertyUtils.getPropertyWithValue(props, allRunsNamespace, "runName",
				runName);
		String currentRunNamespace = StringUtils.substringBefore(nameKey, "runName");

		return currentRunNamespace;
	}

	public static String getCorpusNamespace(Properties props, String corpusName)
			throws PropertiesException {

		String allCorpora = "parallelCorpus.";
		String nameKey = PropertyUtils.getPropertyWithValue(props, allCorpora, "corpusName",
				corpusName);
		String currentRunNamespace = StringUtils.substringBefore(nameKey, "corpusName");

		return currentRunNamespace;
	}

	public static String getInputRunName(Properties props, String runNamespace) {
		String inputRunKey = props.getProperty(runNamespace + "transform.inputRun");
		return props.getProperty(inputRunKey);
	}

	public static File getCorpusRootDirectory(Properties props, String corpusName)
			throws PropertiesException {
		String corpusNamespace = getCorpusNamespace(props, corpusName);
		String corpusRootDirKey = corpusNamespace + "rootdir";
		String corpusRootDir = props.getProperty(corpusRootDirKey);
		return new File(corpusRootDir);
	}
}
