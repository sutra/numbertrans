package info.jonclark.corpus;

import java.io.File;

import info.jonclark.util.FileUtils;
import info.jonclark.util.StringUtils;

/**
 * A quick hack to make rename text files of a different encoding
 * 
 */
public class Renamer {
	public static void main(String... args) throws Exception {
		
		System.out.println("Finding files...");
		File[] files = FileUtils.getFilesFromWildcard(args[0]);
		System.out.println("Found " + files.length + " files to rename.");

		for (final File file : files) {
			if (file.getName().endsWith(".txt")) {
				String newName = StringUtils.substringBefore(file.getName(),
						".txt")
						+ ".orig.txt";
				file.renameTo(new File(file.getParentFile(), newName));
			}
		}
	}
}
