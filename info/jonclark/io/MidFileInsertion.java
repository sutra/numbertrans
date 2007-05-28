/*
 * Created on Apr 28, 2007
 */
package info.jonclark.io;

import info.jonclark.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Allows text to be inserted into the "middle" of a file where "middle" means a
 * location marked by a user-defined string.
 * 
 * @author Jonathan
 */
public class MidFileInsertion {

    private final BufferedReader in;
    private final PrintWriter out;
    private final String strRemainder;

    public MidFileInsertion(InputStream inStream, OutputStream outStream, String marker)
	    throws IOException {
	this.in = new BufferedReader(new InputStreamReader(inStream));
	this.out = new PrintWriter(outStream);

	String line = in.readLine();
	while (line != null && !line.contains(marker)) {
	    out.println(line);
	    line = in.readLine();
	}
	
	if(line == null) {
	    throw new RuntimeException("Marker not found: " + marker);
	}
	
	out.print(StringUtils.substringBefore(line, marker));
	strRemainder = StringUtils.substringAfter(line, marker);
    }

    public void insert(String str) {
	out.print(str);
    }

    public void insertln(String line) {
	out.println(line);
    }

    public void close() throws IOException {
	out.println(strRemainder);
	String line = in.readLine();
	while(line != null) {
	    out.println(line);
	    line = in.readLine();
	}
	in.close();
	out.flush();
	out.close();
    }
}
