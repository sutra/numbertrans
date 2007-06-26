package info.jonclark.corpus.management;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.logging.Logger;

import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.etc.CorpusProperties;
import info.jonclark.corpus.management.iterators.impl.IteratorFactory;
import info.jonclark.corpus.management.iterators.interfaces.CorpusIterator;
import info.jonclark.corpus.management.iterators.interfaces.ParallelCorpusAlignmentIterator;
import info.jonclark.corpus.management.iterators.interfaces.ParallelCorpusCreationIterator;
import info.jonclark.corpus.management.iterators.interfaces.ParallelCorpusTransformIterator;
import info.jonclark.corpus.management.iterators.interfaces.UniCorpusCreationIterator;
import info.jonclark.corpus.management.iterators.interfaces.UniCorpusTransformIterator;
import info.jonclark.corpus.management.runs.ParallelCorpusAlignmentRun;
import info.jonclark.corpus.management.runs.ParallelCorpusCreationRun;
import info.jonclark.corpus.management.runs.ParallelCorpusTransformRun;
import info.jonclark.corpus.management.runs.UniCorpusCreationRun;
import info.jonclark.corpus.management.runs.UniCorpusTransformRun;
import info.jonclark.log.LogUtils;
import info.jonclark.properties.PropertyUtils;

/**
 * Used to invoke "runs" on the corpus
 */
public class CorpusManager {

    private static final Logger log = LogUtils.getLogger();

    private static void processRun(Properties props, String runNamespace)
	    throws Throwable {

	String runName = CorpusProperties.getRunName(runNamespace);
	String corpusName = CorpusProperties.getCorpusNameFromRun(props, runName);

	log.info("Processing run: " + runNamespace);

	// get the corpus run processor
	String processorName = null;
	String type = null;
	try {
	    processorName = CorpusProperties.getRunProcessor(props, runNamespace);
	    Class clazz = Class.forName(processorName);
	    Constructor constructor = clazz.getConstructor(new Class[] { Properties.class,
		    String.class, String.class });
	    Object[] args = new Object[] { props, runName, corpusName };
	    Object instance = constructor.newInstance(args);

	    CorpusIterator iterator = IteratorFactory.getIterator(props, runNamespace);

	    type = CorpusProperties.getRunType(props, runNamespace);
	    if (type.equals("uni.create")) {
		UniCorpusCreationRun processor = (UniCorpusCreationRun) instance;
		processor.processCorpus((UniCorpusCreationIterator) iterator);
	    } else if (type.equals("uni.transform")) {
		UniCorpusTransformRun processor = (UniCorpusTransformRun) instance;
		processor.processCorpus((UniCorpusTransformIterator) iterator);
	    } else if (type.equals("parallel.align")) {
		ParallelCorpusAlignmentRun processor = (ParallelCorpusAlignmentRun) instance;
		processor.processCorpus((ParallelCorpusAlignmentIterator) iterator);
	    } else if (type.equals("parallel.create")) {
		ParallelCorpusCreationRun processor = (ParallelCorpusCreationRun) instance;
		processor.processCorpus((ParallelCorpusCreationIterator) iterator);
	    } else if (type.equals("parallel.transform")) {
		ParallelCorpusTransformRun processor = (ParallelCorpusTransformRun) instance;
		processor.processCorpus((ParallelCorpusTransformIterator) iterator);
	    } else {
		throw new CorpusManException("Unknown run type: " + type);
	    }

	    iterator.validate();

	} catch (NumberFormatException e) {
	    throw e;
	} catch (IllegalArgumentException e) {
	    // XXX: still used?
	    throw new CorpusManException(
		    "Class must define a constructor that takes arguments Properties,"
			    + " String (runName), String (corpusName): " + processorName, e);
	} catch (ClassCastException e) {
	    throw new CorpusManException(
		    "Class must implement the CorpusRun interface corresponding to type " + type
			    + ": " + processorName, e);
	} catch (SecurityException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (NoSuchMethodException e) {
	    throw new CorpusManException(
		    "Class must define a constructor that takes arguments Properties,"
			    + " String (runName), String (corpusName): " + processorName, e);
	} catch(InvocationTargetException e) {
	    throw e.getCause();
	}
    }

    private static void processRunSet(Properties props, String runsetNamespace)
	    throws Throwable {

	log.info("Processing run set: " + runsetNamespace);

	String[] runs = CorpusProperties.getRunsInRunSet(props, runsetNamespace);
	for (final String runName : runs) {
	    String runNamespace = CorpusProperties.getRunNamespace(props, runName);
	    processRun(props, runNamespace);
	}
    }

    public static void main(String[] args) throws Throwable {
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
