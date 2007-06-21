package info.jonclark.corpus.management;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.etc.CorpusProperties;
import info.jonclark.corpus.management.etc.CorpusRun;
import info.jonclark.corpus.management.iterators.impl.IteratorFactory;
import info.jonclark.corpus.management.iterators.interfaces.CorpusIterator;
import info.jonclark.properties.PropertyUtils;

/**
 * Used to invoke "runs" on the corpus
 */
public class CorpusManager {

    private static void processRun(Properties props, String runNamespace)
	    throws IllegalArgumentException, InstantiationException, IllegalAccessException,
	    InvocationTargetException, ClassNotFoundException, CorpusManException {

	// get the corpus run processor
	String processorName = CorpusProperties.getRunProcessor(props, runNamespace);
	Class clazz = Class.forName(processorName);
	Constructor[] constructors = clazz.getConstructors();
	Object[] args = new Object[] { props };
	CorpusRun processor = (CorpusRun) constructors[0].newInstance(args);

	// get the iterator for this processor
	CorpusIterator iterator = IteratorFactory.getIterator(props, runNamespace);

	processor.processCorpus(props, iterator);
    }

    private static void processRunSet(Properties props, String runsetNamespace)
	    throws IllegalArgumentException, InstantiationException, IllegalAccessException,
	    InvocationTargetException, ClassNotFoundException, CorpusManException {
	String[] runs = CorpusProperties.getRunsInRunSet(props, runsetNamespace);
	for (final String runName : runs) {
	    String runNamespace = CorpusProperties.getRunNamespace(props, runName);
	    processRun(props, runNamespace);
	}
    }

    public static void main(String[] args) throws Exception {
	if (args.length != 2) {
	    System.err.println("Usage: CorpusManager <properties_file> <run_set_name_or_run_name>");
	    System.err.println("Example: CorpusManager corpus.properties runSet.ALL");
	    System.exit(1);
	}

	final String propsFile = args[0];
	final String namespace = args[1];

	final Properties props = PropertyUtils.getProperties(propsFile);
	if (namespace.startsWith("runSet.")) {
	    processRunSet(props, namespace);
	} else if (namespace.startsWith("run.")) {
	    processRun(props, namespace);
	} else {
	    System.err.println("Invalid argument " + namespace
		    + " does not start with runSet. or run.");
	    System.exit(1);
	}
    }
}
