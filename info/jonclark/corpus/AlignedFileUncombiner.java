package info.jonclark.corpus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class AlignedFileUncombiner {
    public static void main(String[] args) throws Exception {
	if (args.length != 3) {
	    System.err.println("Usage: program <combinedFileIn> <eFileOut> <fFileOut>");
	    System.exit(1);
	}

	String encoding = "UTF8";
	BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]),
		encoding));
	PrintWriter e = new PrintWriter(args[1], encoding);
	PrintWriter f = new PrintWriter(args[2], encoding);

	String eLine;
	while ((eLine = in.readLine()) != null) {
	    String fLine = in.readLine();

	    e.println(eLine);
	    f.println(fLine);

	    String blank = in.readLine();
	    if (blank == null)
		break;
	}

	e.close();
	f.close();
	in.close();
    }
}
