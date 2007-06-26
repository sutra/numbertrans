package info.jonclark.corpus.management.etc;

import info.jonclark.properties.PropertiesException;
import info.jonclark.properties.PropertyUtils;
import info.jonclark.util.ArrayUtils;
import info.jonclark.util.StringUtils;

import java.io.File;
import java.util.Properties;

public class CorpusProperties {

    public static String getRunNamespace(Properties props, String runName) {
	return StringUtils.forceSuffix("run." + runName, ".");
    }

    public static String getRunName(String runNamespace) {
	String runName = StringUtils.substringAfter(runNamespace, "run.");
	runName = StringUtils.removeTrailingString(runName, ".");
	return runName;
    }

    public static String getRunSetNamespace(Properties props, String runSetName) {
	return StringUtils.forceSuffix("runSet." + runSetName, ".");
    }

    public static String getRunSetName(String runSetNamespace) {
	String runSetName = StringUtils.substringAfter(runSetNamespace, "runSet.");
	runSetName = StringUtils.removeTrailingString(runSetName, ".");
	return runSetName;
    }

    public static String getCorpusNamespace(Properties props, String corpusName) {
	return StringUtils.forceSuffix("corpus." + corpusName, ".");
    }

    public static String getCorpusName(String corpusNamespace) {
	String corpusName = StringUtils.substringAfter(corpusNamespace, "corpus.");
	corpusName = StringUtils.removeTrailingString(corpusName, ".");
	return corpusName;
    }

    public static String[] getRunsInRunSet(Properties props, String runsetNamespace)
	    throws CorpusManException {

	String runs = props.getProperty(runsetNamespace + ".runs");
	if (runs == null)
	    throw new CorpusManException("No such runset: " + runsetNamespace);
	return StringUtils.tokenize(runs);
    }

    public static String getRunProcessor(Properties props, String runNamespace)
	    throws CorpusManException {
	runNamespace = StringUtils.forceSuffix(runNamespace, ".");
	String processor = props.getProperty(runNamespace + "processor");
	if (processor == null)
	    throw new CorpusManException("No such run: " + runNamespace);
	return processor.trim();
    }

    public static String getRunType(Properties props, String runNamespace)
	    throws CorpusManException {
	runNamespace = StringUtils.forceSuffix(runNamespace, ".");
	String processor = props.getProperty(runNamespace + "type");
	if (processor == null)
	    throw new CorpusManException("Type not defined for run " + runNamespace);
	return processor.trim();
    }

    public static String getInputRunName(Properties props, String outputRunName)
	    throws CorpusManException {
	String outputRunNamespace = getRunNamespace(props, outputRunName);
	String inputRunKey = PropertyUtils.getPropertyInNamespaceThatEndsWith(props,
		outputRunNamespace, ".inputRun");
	if (inputRunKey == null)
	    throw new CorpusManException("No input run specified for output run: " + outputRunName);
	String inputRunName = props.getProperty(inputRunKey).trim();
	return inputRunName;
    }

    public static int getAutoNumberFilesPerDir(Properties props, String directoryNamespace)
	    throws CorpusManException {
	directoryNamespace = StringUtils.forceSuffix(directoryNamespace, ".");
	String inputRunKey = directoryNamespace + "autonumber.filesPerDir";
	String value = props.getProperty(inputRunKey);
	if (value == null)
	    throw new CorpusManException("filesPerDir not specified for autonumber directory: "
		    + directoryNamespace);
	return Integer.parseInt(value.trim());
    }

    public static String getAutoNumberPattern(Properties props, String directoryNamespace)
	    throws CorpusManException {
	directoryNamespace = StringUtils.forceSuffix(directoryNamespace, ".");
	String inputRunKey = directoryNamespace + "autonumber.pattern";
	String value = props.getProperty(inputRunKey);
	if (value == null)
	    throw new CorpusManException(
		    "No autonumber pattern specified for autonumber directory: "
			    + directoryNamespace);
	return value;
    }

    /**
         * Defaults to false
         */
    public static boolean getAutoNumberArrangeByFilename(Properties props, String namespace)
	    throws CorpusManException {
	namespace = StringUtils.forceSuffix(namespace, ".");
	String inputRunKey = namespace + "autonumber.arrangeByFilename";
	String value = props.getProperty(inputRunKey, "false");
	return Boolean.parseBoolean(value);
    }

    public static String getNodeFilenamePattern(Properties props, String runNamespace)
	    throws CorpusManException {
	runNamespace = StringUtils.forceSuffix(runNamespace, ".");
	String inputRunKey = runNamespace + "create.filenamePattern";
	String value = props.getProperty(inputRunKey);
	if (value == null)
	    throw new CorpusManException("Filename pattern not specified for node directory: "
		    + runNamespace);
	return value;
    }

    public static boolean hasNodeFilenamePattern(Properties props, String runName) {
	String runNamespace = getRunNamespace(props, runName);
	String inputRunKey = runNamespace + "create.filenamePattern";
	String value = props.getProperty(inputRunKey);
	return (value != null);
    }

    public static File getCorpusRootDirectoryFile(Properties props, String corpusName)
	    throws CorpusManException {
	String corpusNamespace = getCorpusNamespace(props, corpusName);
	String corpusRootDirKey = corpusNamespace + "rootDir";
	String corpusRootDir = props.getProperty(corpusRootDirKey);
	if (corpusRootDir == null)
	    throw new CorpusManException("Root directory location not defined for corpus: "
		    + corpusName);
	return new File(corpusRootDir);
    }

    /**
         * NOTE: This method may return null if the directory is a node
         * directory
         */
    public static String getSubdirectory(Properties props, String directoryNamespace)
	    throws CorpusManException {
	directoryNamespace = StringUtils.forceSuffix(directoryNamespace, ".");
	String corpusRootDirKey = directoryNamespace + "subdir.name";
	String subdir = props.getProperty(corpusRootDirKey);
	if (subdir == null && !getCorpusDirectoryType(props, directoryNamespace).equals("node"))
	    throw new CorpusManException("Subdirectory not defined for non-node directory: "
		    + directoryNamespace);
	return subdir;
    }

    public static String getParallelAlignmentDest(Properties props, String runName)
	    throws CorpusManException {
	String runNamespace = getRunNamespace(props, runName);
	runNamespace = StringUtils.removeTrailingString(runNamespace, ".");
	String parallelDest = props.getProperty(runNamespace + ".align.parallelDest");
	if (parallelDest == null) {
	    throw new CorpusManException("You must specify an align.parallelDest"
		    + "(which parallel directory the aligned files will be put in) for " + runName);
	}
	return parallelDest;
    }

    public static String[] getParallelTargets(Properties props, String directoryNamespace)
	    throws CorpusManException {
	directoryNamespace = StringUtils.forceSuffix(directoryNamespace, ".");

	String value = props.getProperty(directoryNamespace + "parallel.targets");
	if (value == null)
	    throw new CorpusManException("Parallel targets not defined for directory: "
		    + directoryNamespace);
	return StringUtils.tokenize(value);
    }

    public static String getCorpusDirectoryType(Properties props, String directoryNamespace)
	    throws CorpusManException {
	directoryNamespace = StringUtils.forceSuffix(directoryNamespace, ".");
	String type = props.getProperty(directoryNamespace + "type");
	if (type == null)
	    throw new CorpusManException("Directory type not defined for directory: "
		    + directoryNamespace);
	return type;
    }

    public static String getDirectoryName(Properties props, String directoryNamespace)
	    throws CorpusManException {

	String directory = directoryNamespace;
	if (directory.endsWith("."))
	    directory = directory.substring(0, directory.length() - 1);
	directory = StringUtils.substringAfterLast(directory, ".");

	return directory;
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

    public static String getCorpusNameFromRun(Properties props, String runName)
	    throws CorpusManException {
	String runNamespace = getRunNamespace(props, runName);
	String corpusName = props.getProperty(runNamespace + "corpus");
	if (corpusName == null)
	    throw new CorpusManException("Corpus name not specified for run: " + runName);
	return corpusName;
    }

    /**
         * NOTE: This method may return null if the we're not dealing with a
         * parallel corpus
         */
    public static String getUniCreateParallelDest(Properties props, String runName)
	    throws CorpusManException {
	String runNamespace = getRunNamespace(props, runName);
	runNamespace = StringUtils.forceSuffix(runNamespace, ".");
	String parallelDest = props.getProperty(runNamespace + "create.parallelDest");
	return parallelDest;
    }

    public static int getParallelIndex(Properties props, String corpusName, String parallelDirName)
	    throws CorpusManException {
	// first get a list of all parallel dirs to check against
	String parallelNamespace = CorpusProperties.findParallelNamespace(props, corpusName);
	String[] allParallelDirs = CorpusProperties.getParallelTargets(props, parallelNamespace);
	int index = ArrayUtils.findInUnsortedArray(allParallelDirs, parallelDirName);
	return index;
    }

    public static String[] getUniCorpusTransformParallelDirs(Properties props, String runName)
	    throws CorpusManException {
	String runNamespace = getRunNamespace(props, runName);
	runNamespace = StringUtils.forceSuffix(runNamespace, ".");
	String targets = props.getProperty(runNamespace + "transform.parallelDirs");
	if (targets == null)
	    throw new CorpusManException(
		    "No parallel directories specified for uni.transform run: " + runName);
	return StringUtils.tokenize(targets);
    }

    public static String getParallelE(Properties props, String runName) throws CorpusManException {
	String runNamespace = getRunNamespace(props, runName);
	runNamespace = StringUtils.forceSuffix(runNamespace, ".");
	String e = props.getProperty(runNamespace + "parallel.e");
	if (e == null)
	    throw new CorpusManException("E parallel directory not specified for run: " + runName);
	return e;
    }

    public static String getParallelF(Properties props, String runName) throws CorpusManException {
	String runNamespace = getRunNamespace(props, runName);
	runNamespace = StringUtils.forceSuffix(runNamespace, ".");
	String f = props.getProperty(runNamespace + "parallel.f");
	if (f == null)
	    throw new CorpusManException("F parallel directory not specified for run: " + runName);
	return f;
    }

    public static int getParallelIndexE(Properties props, String corpusName, String outputRunName)
	    throws CorpusManException {
	String desiredParallelDirE = CorpusProperties.getParallelE(props, outputRunName);
	return CorpusProperties.getParallelIndex(props, corpusName, desiredParallelDirE);
    }

    public static int getParallelIndexF(Properties props, String corpusName, String outputRunName)
	    throws CorpusManException {
	String desiredParallelDirF = CorpusProperties.getParallelF(props, outputRunName);
	return CorpusProperties.getParallelIndex(props, corpusName, desiredParallelDirF);
    }

    public static boolean getArrangeByFilename(Properties props, String corpusName)
	    throws CorpusManException {
	String corpusNamespace = getCorpusNamespace(props, corpusName);
	String key = PropertyUtils.getPropertyInNamespaceThatEndsWith(props, corpusNamespace,
		"autonumber.arrangeByFilename");
	if (key == null)
	    throw new CorpusManException(
		    "Property autonumber.arrangeByFilename not defined for corups: " + corpusName);
	String value = props.getProperty(key);
	return Boolean.parseBoolean(value);
    }
}
