/*
 * Created on Jun 9, 2007
 */
package info.jonclark.corpus.management.documents;

import info.jonclark.corpus.management.etc.CorpusManRuntimeException;
import info.jonclark.util.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Abstracts the location of a corpus document from the user so that tasks such
 * as distributed processing become easy.
 */
public class InputDocument implements CloseableDocument {

    private BufferedReader in;
    protected final File file;
    private boolean closed = false;
    protected final Charset encoding;
    private final MetaDocument meta;

    public InputDocument(File file, MetaDocument meta, Charset encoding) {
	assert file.exists() : "File does not exist: " + file.getAbsolutePath();
	// TODO: Handle GZIP
	this.file = file;
	this.encoding = encoding;
	this.meta = meta;
    }

    public InputDocument(URL file) {
	throw new Error("Unimplemented. Please wait for our distributed architecture.");
    }

    protected BufferedReader getBufferedReader() throws FileNotFoundException {
	return new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
    }

    /**
         * Get a document that can store meta data about this document. The meta
         * data is the same for both the input and output documents and is
         * global accross all runs.
         */
    public MetaDocument getMetaDocument() {
	return meta;
    }

    /**
         * Do lazy initialization of the writer so that we can do non-closed
         * file detection properly.
         */
    private void open() {
	if (this.in == null) {
	    closed = false;

	    try {
		this.in = getBufferedReader();
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
	return FileUtils.getFileAsString(file, encoding);
    }

    public void copyTo(OutputDocument out) throws IOException {
	out.copyFrom(this);
    }

    /**
         * NOTE: You are discouraged from using this method unless absolutely
         * necessary. Under certain conditions, it is less efficient and safe
         * than other methods.
         * <p>
         * OutputDocument has no counterpart to this method since it would be
         * completely unsafe.
         */
    public File getFile() {
	return file;
    }

    public void close() throws IOException {
	closed = true;
	if (in != null)
	    in.close();
    }

    public boolean isClosed() {
	return closed;
    }
}
