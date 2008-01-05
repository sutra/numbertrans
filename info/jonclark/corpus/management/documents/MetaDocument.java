package info.jonclark.corpus.management.documents;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * A class representing metadata that follows a corpus document throughout its
 * lifetime (including between runs).
 */
public class MetaDocument implements CloseableDocument {

    private Properties props;
    private File file;
    private boolean closed = true;
    private boolean changed = false;

    public MetaDocument(File file) {
	this.file = file;
    }

    private void open() throws IOException {
	closed = false;
	this.props = new Properties();
	if (file.exists()) {
	    InputStream in = new FileInputStream(file);
	    file.createNewFile();
	    props.load(in);
	    in.close();
	}
    }

    public void set(String key, String value) throws IOException {
	open();
	changed = false;
	props.setProperty(key, value);
    }

    public String get(String key) throws IOException {
	open();
	return props.getProperty(key);
    }

    public void close() throws IOException {
	if (changed) {
	    OutputStream out = new FileOutputStream(file);
	    props.store(out, "");
	    out.close();
	    changed = false;
	}
	closed = true;
    }

    public boolean isClosed() {
	return closed;
    }
}
