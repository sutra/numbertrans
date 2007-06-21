/*
 * Created on Jun 9, 2007
 */
package info.jonclark.corpus.management.etc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;

/**
 * Abstracts the location of a corpus document from the user so that tasks such
 * as distributed processing become easy.
 */
public class InputDocument {
    
    private final File file;
    
    public InputDocument(File file) {
	// TODO: Handle GZIP
	this.file = file;
    }
    
    public InputDocument(URL file) {
	throw new Error("Unimplemented. Please wait for our distributed architecture.");
    }
    
    public BufferedReader getBufferedReader() {
	try {
	    return new BufferedReader(new FileReader(file));
	} catch (FileNotFoundException e) {
	    throw new RuntimeException("File should exist but does not. Deleted during runtime?", e);
	}
    }
}
