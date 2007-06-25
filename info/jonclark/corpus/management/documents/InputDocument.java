/*
 * Created on Jun 9, 2007
 */
package info.jonclark.corpus.management.documents;

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

    private final BufferedReader in;
    private boolean closed = false;

    public InputDocument(File file) {
	try {
	    assert file.exists() : "File does not exist: " + file.getAbsolutePath();
	    // TODO: Handle GZIP
	    this.in = new BufferedReader(new FileReader(file));
	} catch (FileNotFoundException e) {
	    throw new RuntimeException("File should exist but does not. Deleted during runtime?", e);
	}
    }

    public InputDocument(URL file) {
	throw new Error("Unimplemented. Please wait for our distributed architecture.");
    }

    public String readLine() throws IOException {
	return in.readLine();
    }

    public void close() throws IOException {
	closed = true;
	in.close();
    }

    public boolean isClosed() {
	return closed;
    }
}
