/*
 * Created on Dec 6, 2006
 */
package info.jonclark.sourcecode;

import info.jonclark.util.ArrayUtils;
import info.jonclark.util.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class SourceLineCounter extends CommentProcessor {

    private final static boolean VERBOSE = false;
    private int nFileLines = 0;
    private int nFiles = 0;
    private boolean lineCountedOnce = false;
    private String[] ignoreDirectories;

    public SourceLineCounter(String... ignoreDirectories) {
	super(false, false);
	
	this.ignoreDirectories = ignoreDirectories;
	Arrays.sort(ignoreDirectories);
    }

    /*
         * Count lines of all files in a directory structure
         */
    public int countLinesRecursively(File root, boolean includeHiddenDirs, String... ext)
	    throws IOException {
	int nDirectoryLines = 0;
//	System.out.println("Reading directory: " + root.getAbsolutePath());
	assert root.exists() : "dir doesn't exist";
	final File[] files = FileUtils.getFilesWithExt(root, ext);
	for (final File file : files)
	    nDirectoryLines += countLinesInFile(file);
	final File[] directories = FileUtils.getSubdirectories(root);
	for (final File dir : directories)
	    if (includeHiddenDirs || !dir.getName().startsWith("."))
		if(Arrays.binarySearch(ignoreDirectories, dir.getName()) < 0)
		    nDirectoryLines += countLinesRecursively(dir, includeHiddenDirs, ext);

	System.out.println(root.getAbsolutePath() + " (" + nDirectoryLines + " lines)");

	return nDirectoryLines;
    }

    public int countLinesInFile(File file) throws IOException {
	nFiles++;
	nFileLines = 0;
	final BufferedReader in = new BufferedReader(new InputStreamReader(
		new FileInputStream(file), "UTF-8"));
	String line;
	while ((line = in.readLine()) != null) {
	    line = line.trim();
	    if (!line.equals("")) {
		lineCountedOnce = false;
		processLine(line);
		if(VERBOSE && lineCountedOnce)
		    System.out.println(line);
	    }
	}

	System.out.println(file.getName() + " (" + nFileLines + " lines)");
	return nFileLines;
    }

    @Override
    protected String processComment(String line) {
	return line;
    }

    @Override
    protected String processNoncomment(String line) {
	if (!lineCountedOnce)
	    nFileLines++;
	lineCountedOnce = true;
	return line;
    }
    
    public int getFileCount() {
	return nFiles;
    }

    /**
         * @param args
         * @throws IOException
         */
    public static void main(String... args) throws IOException {
	SourceLineCounter c = new SourceLineCounter("sphinx4", "log4net-1.2.10", "TestTools", "wxWidgets", "ui");
	int nTotalLines = c.countLinesRecursively(new File(args[0]), false, ".java", ".cs", ".cpp", ".h", ".c");
	int nFiles = c.getFileCount();
//	int nTotalLines = c.countLinesInFile(new File(args[0]));
	System.out.println(nTotalLines + " non-comment, non-blank source lines in " + nFiles + " files for "+ args[0]);
    }

}
