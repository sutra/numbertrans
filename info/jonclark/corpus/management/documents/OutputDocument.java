/*
 * Created on Jun 9, 2007
 */
package info.jonclark.corpus.management.documents;

import info.jonclark.corpus.management.etc.CorpusManRuntimeException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URL;

/**
 * Abstracts the location of a corpus document from the user so that tasks such
 * as distributed processing become easy.
 */
public class OutputDocument {

    private PrintWriter writer;
    private final File file;
    private boolean closed = true;

    public OutputDocument(File file) {
	// TODO: Handle GZIP
	assert file.getParentFile().exists() : "Parent directory does not exist: "
		+ file.getAbsolutePath();
	this.file = file;
    }

    /**
         * Do lazy initialization of the writer so that we can do non-closed
         * file detection properly.
         */
    private void open() {
	if (this.writer == null) {
	    closed = false;
	    
	    try {
		this.writer = new PrintWriter(file);
	    } catch (FileNotFoundException e) {
		throw new CorpusManRuntimeException(
			"File should exist but does not. Deleted during runtime?", e);
	    }
	}
    }

    public OutputDocument(URL url) {
	throw new Error("Unimplemented. Please wait for our distributed architecture.");
    }

    public void println(String line) {
	open();
	this.writer.println(line);
    }

    public void close() {
	closed = true;
	writer.close();
    }

    public boolean isClosed() {
	return closed;
    }
}
