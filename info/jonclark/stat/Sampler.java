package info.jonclark.stat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Random;

public class Sampler {
    public static void main(String[] args) throws Exception {
	
	double prob = Double.parseDouble(args[0]);
	
	BufferedReader e = new BufferedReader(new FileReader(args[1]));
	BufferedReader f = new BufferedReader(new FileReader(args[2]));
	
	PrintWriter eYes = new PrintWriter(args[1] + ".yes");
	PrintWriter eNo = new PrintWriter(args[1] + ".no");
	PrintWriter fYes = new PrintWriter(args[2] + ".yes");
	PrintWriter fNo = new PrintWriter(args[2] + ".no");
	
	Random rand = new Random();
	
	String eLine;
	String fLine;
	while ((eLine = e.readLine()) != null && (fLine = f.readLine()) != null) {
	    if(prob < rand.nextDouble()) {
		eYes.println(eLine);
		fYes.println(fLine);
	    } else {
		eNo.println(eLine);
		fNo.println(fLine);
	    }
	}
	e.close();
	f.close();
	eYes.close();
	eNo.close();
	fYes.close();
	fNo.close();
    }
}
