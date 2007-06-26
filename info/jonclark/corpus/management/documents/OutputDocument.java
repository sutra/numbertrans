/*
 * Created on Jun 9, 2007
 */
package info.jonclark.corpus.management.documents;

import info.jonclark.corpus.management.etc.CorpusManRuntimeException;
import info.jonclark.io.LineWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.channels.FileChannel;

/**
 * Abstracts the location of a corpus document from the user so that tasks such
 * as distributed processing become easy.
 */
public class OutputDocument implements LineWriter {

    private PrintWriter writer;
    private boolean closed = true;
    protected final File file;

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
    
    public void copyFrom(InputDocument doc) throws IOException {
	boolean wasClosed = closed;
	
	closed = false;
	final FileChannel sourceChannel = new FileInputStream(doc.file).getChannel();
	final FileChannel destinationChannel = new FileOutputStream(this.file).getChannel();

	sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);

	sourceChannel.close();
	destinationChannel.close();
	
	closed = wasClosed;
    }

    public void close() {
	closed = true;
	if(writer != null)
	    writer.close();
    }

    public boolean isClosed() {
	return closed;
    }
}
