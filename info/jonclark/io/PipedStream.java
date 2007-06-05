package info.jonclark.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;


public class PipedStream extends Thread {

	private final InputStream in;
	private final OutputStream out;

	public PipedStream(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
	}

	public void run() {
		final BufferedReader br = new BufferedReader(new InputStreamReader(in));
		final PrintWriter pr = new PrintWriter(out);
		try {
			String line;
			while ( (line = br.readLine()) != null)
				pr.println(line);
		} catch (IOException e) {
			// we really don't care since this is a black hole
			e.printStackTrace();
		}
	}
}
