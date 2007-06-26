/*
 * Created on Jun 9, 2007
 */
package info.jonclark.corpus.management.documents;

import info.jonclark.corpus.management.etc.CorpusManRuntimeException;
import info.jonclark.util.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

/**
 * Abstracts the location of a corpus document from the user so that tasks such
 * as distributed processing become easy.
 */
public class InputDocument {

    private BufferedReader in;
    protected final File file;
    private boolean closed = false;

    public InputDocument(File file) {
	assert file.exists() : "File does not exist: " + file.getAbsolutePath();
	// TODO: Handle GZIP
	this.file = file;
    }

    public InputDocument(URL file) {
	throw new Error("Unimplemented. Please wait for our distributed architecture.");
    }

    /**
         * Do lazy initialization of the writer so that we can do non-closed
         * file detection properly.
         */
    private void open() {
	if (this.in == null) {
	    closed = false;

	    try {
		this.in = new BufferedReader(new FileReader(file));
	    } catch (FileNotFoundException e) {
		throw new CorpusManRuntimeException(
			"File should exist but does not. Deleted during runtime?", e);
	    }
	}
    }

    public String readLine() throws IOException {
	open();
	return in.readLine();
    }
    
    public String getWholeFile() throws IOException {
	return FileUtils.getFileAsString(file);
    }
    
    public void copyTo(OutputDocument out) throws IOException {
	out.copyFrom(this);
    }

    public void close() throws IOException {
	closed = true;
	if(in != null)
	    in.close();
    }

    public boolean isClosed() {
	return closed;
    }
}
