package info.jonclark.corpus.management.etc;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

import info.jonclark.corpus.management.directories.AbstractCorpusDirectory;
import info.jonclark.properties.PropertyUtils;
import info.jonclark.properties.PropertiesException;
import info.jonclark.util.StringUtils;

public class CorpusProperties {

    public static String getRunNamespace(Properties props, String runName) {
	return "run." + runName + ".";
    }

    public static String getRunSetNamespace(Properties props, String runSetName) {
	return "runSet." + runSetName + ".";
    }

    public static String getCorpusNamespace(Properties props, String corpusName) {
	return "corpus." + corpusName + ".";
    }

    public static String getInputRunName(Properties props, String outputRunName) {
	String outputRunNamespace = getRunNamespace(props, outputRunName);
	String inputRunKey = PropertyUtils.getPropertyInNamespaceThatEndsWith(props,
		outputRunNamespace, ".inputRun");
	return props.getProperty(inputRunKey);
    }

    public static int getAutoNumberFilesPerDir(Properties props, String namespace) {
	namespace = StringUtils.forceSuffix(namespace, ".");
	String inputRunKey = props.getProperty(namespace + "autonumber.filesPerDir");
	String value = props.getProperty(inputRunKey);
	return Integer.parseInt(value);
    }

    public static String getAutoNumberPattern(Properties props, String namespace) {
	namespace = StringUtils.forceSuffix(namespace, ".");
	String inputRunKey = props.getProperty(namespace + "autonumber.pattern");
	return props.getProperty(inputRunKey);
    }

    public static boolean getAutoNumberArrangeByFilename(Properties props, String namespace) {
	namespace = StringUtils.forceSuffix(namespace, ".");
	String inputRunKey = props.getProperty(namespace + "autonumber.arrangeByFilename");
	String value = props.getProperty(inputRunKey);
	return Boolean.parseBoolean(value);
    }

    public static String getNodeFilenamePattern(Properties props, String namespace) {
	namespace = StringUtils.forceSuffix(namespace, ".");
	String inputRunKey = props.getProperty(namespace + "node.filenamePattern");
	String value = props.getProperty(inputRunKey);
	return value;
    }

    public static File getCorpusRootDirectoryFile(Properties props, String corpusName) {
	String corpusNamespace = getCorpusNamespace(props, corpusName);
	String corpusRootDirKey = corpusNamespace + "rootdir";
	String corpusRootDir = props.getProperty(corpusRootDirKey);
	return new File(corpusRootDir);
    }

    public static String getSubdirectory(Properties props, String namespace) {
	namespace = StringUtils.forceSuffix(namespace, ".");
	String corpusRootDirKey = namespace + "subdir.name";
	String subdir = props.getProperty(corpusRootDirKey);
	return subdir;
    }

    public static String getParallelAlignmentDest(Properties props, String runName) {
	String runNamespace = getRunNamespace(props, runName);
	String parallelDest = props.getProperty(runNamespace + ".align.parallelDest");
	return parallelDest;
    }

    public static String[] getParallelTargets(Properties props, String directoryNamespace) {
	directoryNamespace = StringUtils.forceSuffix(directoryNamespace, ".");

	ArrayList<String> targets = new ArrayList<String>();
	int i = 0;
	String value = props.getProperty(directoryNamespace + "parallel.target." + i);
	while (value != null) {
	    i++;
	    value = props.getProperty(directoryNamespace + "parallel.target." + i);
	}

	if (i <= 1) {
	    throw new RuntimeException("No targets defined for parallel directory: "
		    + directoryNamespace);
	}

	return targets.toArray(new String[targets.size()]);
    }

    public static String getType(Properties props, String namespace) {
	namespace = StringUtils.forceSuffix(namespace, ".");
	return props.getProperty(namespace + "type");
    }

    public static String getName(Properties props, String namespace) {
	namespace = StringUtils.forceSuffix(namespace, ".");
	return props.getProperty(namespace + "name");
    }

    public static String findParallelNamespace(Properties props, String corpusName)
	    throws CorpusManException {
	try {
	    String corpusNamespace = getCorpusNamespace(props, corpusName);
	    String typeProperty = PropertyUtils.getPropertyWithValue(props, corpusNamespace,
		    ".type", "parallel");
	    String parallelNamespace = StringUtils.substringBefore(typeProperty, ".type");
	    return parallelNamespace;
	} catch (PropertiesException p) {
	    throw new CorpusManException(
		    "Error while attempting to locate parallel namespace in properties file.", p);
	}
    }

    public static String getCorpusNameFromRun(Properties props, String runName) {
	String runNamespace = getRunNamespace(props, runName);
	String corpusName = props.getProperty(runNamespace + ".corpus");
	return corpusName;
    }

    public static String getUniCreateParallelDest(Properties props, String runName) {
	String runNamespace = getRunNamespace(props, runName);
	String corpusName = props.getProperty(runNamespace + ".create.parallelDest");
	return corpusName;
    }

    public static int getParallelIndex(Properties props, String corpusName, String parallelDirName)
	    throws CorpusManException {
	try {
	    String corpusNamespace = getCorpusNamespace(props, corpusName);
	    String targetNamespace = corpusNamespace + ".parallel.target.";
	    String propName = PropertyUtils.getPropertyWithValue(props, targetNamespace,
		    parallelDirName);
	    String index = StringUtils.substringAfter(propName, targetNamespace);
	    int n = Integer.parseInt(index);
	    return n;
	} catch (PropertiesException e) {
	    throw new CorpusManException("Could not determine parallel index for directory "
		    + parallelDirName, e);
	}
    }
    
    public static String[] getUniCorpusTransformParallelDirs(Properties props, String runName) {
	String runNamespace = getRunNamespace(props, runName);
	String targets = props.getProperty(runNamespace + ".transform.parallelDirs");
	return StringUtils.tokenize(targets);
    }
}
