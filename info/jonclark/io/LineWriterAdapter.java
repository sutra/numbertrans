/*
 * Created on Jun 24, 2007
 */
package info.jonclark.io;

import java.io.PrintWriter;

/**
 * Use more traditional classes as a line writer
 */
public class LineWriterAdapter implements LineWriter {
    
    private final PrintWriter w;
    
    public LineWriterAdapter(PrintWriter w) {
	this.w = w;
    }
    
    public void println(String line) {
	this.w.println(line);
    }
}
