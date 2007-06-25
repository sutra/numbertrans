package info.jonclark.sourcecode;

import info.jonclark.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Kills off all .svn directories and their contents in a tree. Fixes accidental
 * adding of files. DO NOT USE if files are already committed. For that problem,
 * you must delete the files from SVN, then use this program on a spearate copy.
 */
public class SvnDataKiller {
    public void findAndDeleteRecursively(File root)
	    throws IOException {
	final File[] directories = FileUtils.getSubdirectories(root);

	for (final File dir : directories) {
	    if (dir.getName().equals(".svn")) {
		System.out.println(dir.getAbsolutePath() + " (removed)");
		killRecursively(dir);
	    } else {
		findAndDeleteRecursively(dir);
	    }
	}

    }
    
    public static void killRecursively(File root) throws IOException {
	final File[] directories = FileUtils.getSubdirectories(root);
	for (final File dir : directories)
	    killRecursively(dir);
	for(final File file : FileUtils.getNormalFiles(root))
	    file.delete();
	root.delete();
    }

    /**
         * @param args
         * @throws IOException
         */
    public static void main(String[] args) throws IOException {
	SvnDataKiller c = new SvnDataKiller();
	c.findAndDeleteRecursively(new File(args[0]));
	System.out.println("Cleaning complete.");
    }

}
