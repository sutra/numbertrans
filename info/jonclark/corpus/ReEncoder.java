package info.jonclark.corpus;

import info.jonclark.corpus.management.documents.InputDocument;
import info.jonclark.corpus.management.documents.OutputDocument;
import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.etc.CorpusProperties;
import info.jonclark.corpus.management.iterators.interfaces.UniCorpusTransformIterator;
import info.jonclark.corpus.management.runs.UniCorpusTransformRun;
import info.jonclark.util.FileUtils;
import info.jonclark.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Properties;

/**
 * This class can run as either a CorpusRun or a standalone program.
 * <p>
 * CorpusRun: uses the properties in the run namespace to determine decode and
 * encode codings.
 * <p>
 * Standalone: Re-encodes text files. Replaces the entire extention with the
 * specified extention
 */
public class ReEncoder implements UniCorpusTransformRun {

    private final String decodeCharset;
    private final String encodeCharset;

    public ReEncoder(Properties props, String runName, String corpusName) {
	String runNamespace = CorpusProperties.getRunNamespace(props, runName);
	this.decodeCharset = props.getProperty(runNamespace + "reencoder.decodeCharset");
	this.encodeCharset = props.getProperty(runNamespace + "reencoder.encodeCharset");
    }

    public void processCorpus(UniCorpusTransformIterator iterator) throws CorpusManException {
	try {
	    while (iterator.hasNext()) {

		iterator.next();

		InputDocument in = iterator.getInputDocument();
		OutputDocument out = iterator.getOutputDocument();

		String line;
		while ((line = in.readLine()) != null) {
		    // switch encoding via reader
		    out.println(line);
		}

		in.close();
		out.close();

	    }
	    iterator.finish();
	} catch (IOException e) {
	    throw new CorpusManException(e);
	}
    }

    public static void main(String... args) throws Exception {

	if (args.length != 4) {
	    System.out.println("Usage: program <input_file_wildcard> <input_encoding> <output_encoding> <out_ext>");
	    System.exit(1);
	}

	String pattern = args[0];
	String decodeCharSet = args[1];
	String encodeCharSet = args[2];

	System.out.println("Finding files...");
	File[] files = FileUtils.getFilesFromWildcard(pattern);
	System.out.println(files.length + " files found.");

	String outExt = args[3];

	for (final File inFile : files) {
	    String outName = StringUtils.substringBefore(inFile.getName(), ".") + outExt;
	    File outFile = new File(inFile.getParentFile(), outName);

	    System.out.println(inFile.getName() + " ==> " + outFile);

	    BufferedReader in = new BufferedReader(new InputStreamReader(
		    new FileInputStream(inFile), decodeCharSet));
	    PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile),
		    encodeCharSet));

	    String line = in.readLine();
	    while (line != null) {
		out.println(line);
		line = in.readLine();
	    }
	    in.close();
	    out.close();
	}
    }
}
