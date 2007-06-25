/*
 * Created on Jun 9, 2007
 */
package info.jonclark.corpus.management.iterators.impl;

import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.etc.CorpusProperties;
import info.jonclark.corpus.management.iterators.interfaces.CorpusIterator;
import info.jonclark.util.StringUtils;

import java.util.Properties;

public class IteratorFactory {
    public static CorpusIterator getIterator(Properties props, String runNamespace)
	    throws CorpusManException {
	
	String type = CorpusProperties.getRunType(props, runNamespace);
	String runName = StringUtils.substringAfter(runNamespace, ".");

	if (type.equals("uni.create")) {
	    return new SimpleUniCorpusCreationIterator(props, runName);
	} else if (type.equals("uni.transform")) {
	    return new SimpleUniTransformIterator(props, runName);
	} else if (type.equals("parallel.create")) {
	    return new SimpleParallelCreationIterator(props, runName);
	} else if (type.equals("parallel.transform")) {
	    return new SimpleParallelTransformIterator(props, runName);
	} else if (type.equals("parallel.align")) {
	    return new SimpleParallelAlignmentIterator(props, runName);
	} else {
	    throw new RuntimeException("Unknown run type: " + type + " for run " + runNamespace);
	}
    }
}
