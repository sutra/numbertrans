package info.jonclark.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ProcessWrapper {
	
	protected Process p;
	protected PrintWriter out;
	protected BufferedReader in;
	
	public ProcessWrapper(String command) throws IOException {
		p = Runtime.getRuntime().exec(command);
		out = new PrintWriter(p.getOutputStream());
		in = new BufferedReader(new InputStreamReader(p.getInputStream()));
	}

	public String send(String input) throws IOException {
		out.println(input);
		return in.readLine();
	}
	
	public void terminate() {
		p.destroy();
	}
}
