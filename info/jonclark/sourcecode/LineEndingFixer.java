/*
 * Created on Dec 6, 2006
 */
package info.jonclark.sourcecode;

import info.jonclark.util.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class LineEndingFixer {

    /*
         * Count lines of all files in a directory structure
         */
    public void fixDirectory(File root, String ext, boolean includeHiddenDirs)
	    throws IOException {

//	System.out.println("Reading directory: " + root.getAbsolutePath());
	final File[] files = FileUtils.getFilesWithExt(root, ext);
	for (final File file : files)
	    fixFile(file);
	final File[] directories = FileUtils.getSubdirectories(root);
	for (final File dir : directories)
	    if (includeHiddenDirs || !dir.getName().startsWith("."))
		fixDirectory(dir, ext, includeHiddenDirs);

//	System.out.println(root.getAbsolutePath());
    }

    public void fixFile(File origFile) throws IOException {
	final BufferedReader in = new BufferedReader(new InputStreamReader(
		new FileInputStream(origFile), "UTF-8"));
	final File tempFile = File.createTempFile("fixLines_", ".tmp");
	final PrintWriter out = new PrintWriter(tempFile);
	String line;
	while ((line = in.readLine()) != null) {
	    out.println(line);
	}
	in.close();
	out.flush();
	out.close();
	
	origFile.delete();
	tempFile.renameTo(origFile);

	System.out.println("Renamed " + tempFile.getName() + " to " + origFile.getName());
    }

    /**
         * @param args
         * @throws IOException
         */
    public static void main(String[] args) throws IOException {
	LineEndingFixer c = new LineEndingFixer();
	c.fixDirectory(new File(args[0]), ".txt", false);
    }

}
