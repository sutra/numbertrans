package info.jonclark.corpus;

import info.jonclark.util.FileUtils;
import info.jonclark.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Re-encodes text files. Replaces the entire extention with the specified
 * extention
 * 
 */
public class ReEncoder {
	public static void main(String... args) throws Exception {

		if (args.length != 4) {
			System.out
					.println("Usage: program <input_file_wildcard> <input_encoding> <output_encoding> <out_ext>");
			System.exit(1);
		}

		String pattern = args[0];
		String decodeCharSet = args[1];
		String encodeCharSet = args[2];

		System.out.println("Finding files...");
		File[] files = FileUtils.getFilesFromWildcard(pattern);
		System.out.println(files.length + " files found.");

		String outExt = args[3];

		for (final File inFile : files) {
			String outName = StringUtils.substringBefore(inFile.getName(), ".")
					+ outExt;
			File outFile = new File(inFile.getParentFile(), outName);

			System.out.println(inFile.getName() + " ==> " + outFile);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(inFile), decodeCharSet));
			PrintWriter out = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(outFile), encodeCharSet));

			String line = in.readLine();
			while (line != null) {
				out.println(line);
				line = in.readLine();
			}
			in.close();
			out.close();
		}
	}
}
