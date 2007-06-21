/*
 * Created on Jun 9, 2007
 */
package info.jonclark.corpus.management.etc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URL;

/**
 * Abstracts the location of a corpus document from the user so that tasks such
 * as distributed processing become easy.
 */
public class OutputDocument {

    private final File file;

    public OutputDocument(File file) {
	// TODO: Handle GZIP
	this.file = file;
    }
    
    public OutputDocument(URL url) {
	throw new Error("Unimplemented. Please wait for our distributed architecture.");
    }

    public PrintWriter getPrintWriter() {
	try {
	    return new PrintWriter(file);
	} catch (FileNotFoundException e) {
	    throw new RuntimeException("File should exist but does not. Deleted during runtime?", e);
	}
    }
}
