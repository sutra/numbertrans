/*
 * Copyright (c) 2006, Jonathan Clark <jon_DOT_h_DOT_clark_AT_gmail_DOT_com> 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of my affiliates nor the names of thier contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIEDWARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package info.jonclark.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Convenience methods for deailing with files
 */
public class FileUtils {

	/**
	 * Copies a file using NIO
	 * 
	 * @param in
	 *            The source file
	 * @param out
	 *            The destination file
	 * @throws IOException
	 */
	public static void copyFile(final File in, final File out) throws IOException {

		final FileChannel sourceChannel = new FileInputStream(in).getChannel();
		final FileChannel destinationChannel = new FileOutputStream(out).getChannel();

		sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);

		sourceChannel.close();
		destinationChannel.close();
	}

	/**
	 * Inserts the entire contents of a file into an open PrintWriter by reading
	 * each line from <code>in</code> and calling <code>out.println()</code>.
	 * 
	 * @param inFile
	 *            The file to be inserted into out.
	 * @param out
	 *            The destination PrintWriter.
	 * @throws IOException
	 *             If an error is encountered in reading or writing.
	 */
	public static void insertFile(final File inFile, final PrintWriter out) throws IOException {
		final BufferedReader in = new BufferedReader(new FileReader(inFile));
		String line = null;
		while ((line = in.readLine()) != null)
			out.println(line);
	}

	/**
	 * Inserts the entire contents of a file into an open PrintWriter by reading
	 * each line from <code>in</code> and calling <code>out.println()</code>.
	 * 
	 * @param inFile
	 *            The file to be inserted into out.
	 * @param out
	 *            The destination PrintWriter.
	 * @param A
	 *            prefix for every line in inFile.
	 * @throws IOException
	 *             If an error is encountered in reading or writing.
	 */
	public static void insertFile(final File inFile, final PrintWriter out,
			final String strLinePrefix) throws IOException {
		final BufferedReader in = new BufferedReader(new FileReader(inFile));
		String line = null;
		while ((line = in.readLine()) != null)
			out.println(strLinePrefix + line);
	}

	/**
	 * Inserts the entire contents of a file into a StringBuilder by reading
	 * each line from <code>in</code> and calling <code>out.append()</code>.
	 * 
	 * @param inFile
	 *            The file to be inserted into out.
	 * @param out
	 *            The destination StringBuilder.
	 * @throws IOException
	 *             If an error is encountered in reading or writing.
	 */
	public static void insertFile(final File inFile, final StringBuilder out) throws IOException {
		final BufferedReader in = new BufferedReader(new FileReader(inFile));
		String line = null;
		while ((line = in.readLine()) != null)
			out.append(line);
	}

	/**
	 * Returns a file with the given path, which is guaranteed to exist on
	 * return
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static File createFileWithPath(final String path) throws IOException {
		File f = new File(path);
		f.getParentFile().mkdirs();
		f.createNewFile();
		return f;
	}

	public static BufferedReader openTextFile(final File f) throws FileNotFoundException {
		// we don't use a string in case we want to check existance
		return new BufferedReader(new FileReader(f));
	}

	public static PrintStream getFileForWriting(final File f) throws FileNotFoundException {
		return new PrintStream(new FileOutputStream(f));
	}

	/**
	 * Adds a trailing slash to a directory path string if and only if the path
	 * string does not already end with a trailing slash. This ensures that a
	 * filename appended to the end of the string will function as a filename
	 * and not an invalid directory name.
	 * 
	 * @param str
	 *            The path string
	 * @return The path string guaranteed to end with a slash
	 */
	public static String forceTrailingSlash(final String str) {
		assert str != null : "str cannot be null";

		if (str.indexOf('/') != str.length() - 1)
			return str + "/";
		else
			return str;
	}

	/**
	 * Adds a leading ./ to a file path string if and only if the path string is
	 * not an absolute path. This is necessary for relative paths that contain
	 * only one file if the getParentFile() method of File class is to work
	 * properly.
	 * 
	 * @param str
	 * @return
	 */
	public static String forceLeadingDotSlash(String str) {
		if (!isAbsolutePath(str))
			return "./" + str;
		else
			return str;
	}

	/**
	 * Determines whether a path string is a relative or absolute path.
	 * 
	 * @param str
	 *            The path string to be analyzed
	 * @return True iff the file is a UNIX, DOS, or Windows absolute path
	 */
	public static boolean isAbsolutePath(final String str) {
		// test for UNIX absolute path
		if (str.startsWith("/"))
			return true;

		// test for DOS/Windows absolute path
		if (str.length() >= 3) {
			return (str.charAt(1) == ':' && (str.charAt(2) == '/' || str.charAt(2) == '\\'));
		} else {
			return false;
		}
	}

	/**
	 * Get files having the specified extention within the specified root
	 * directory.
	 * 
	 * @param ext
	 *            e.g. ".txt"
	 * @return
	 */
	public static File[] getFilesWithExt(final File root, final String... exts) {
		assert root != null : "root must not be null";

		final FilenameFilter filter = new FilenameFilter() {

			public boolean accept(File dir, String name) {
				for (final String ext : exts) {
					if (name.endsWith(ext))
						return true;
				}
				return false;
			}
		};
		return root.listFiles(filter);
	}

	public static boolean isAbsoltePath(String path) {
		return (path.length() >= 1 && path.charAt(0) == '/')
				|| (path.length() >= 2 && path.charAt(1) == ':');
	}

	public static File[] getFilesFromWildcard(String wildcard) throws IOException {

		// normalize slashes
		wildcard = wildcard.replace('\\', '/');

		// strip directory self-reference
		wildcard = StringUtils.replaceFast(wildcard, "./", "");

		// change wildcards into regext format
		wildcard = StringUtils.replaceFast(wildcard, ".", "\\.");
		wildcard = StringUtils.replaceFast(wildcard, "*", ".+?");
		wildcard = StringUtils.replaceFast(wildcard, "?", ".");

		// preserve the part of the path that tells us where to start
		File parentDirectory;
		if (isAbsolutePath(wildcard)) {
			String parent = StringUtils.substringBefore(wildcard, "/", true);
			wildcard = StringUtils.substringAfter(wildcard, "/");
			parentDirectory = new File(parent);
		} else {
			// this is a relative path
			parentDirectory = new File(".");
		}

		String[] wildcardTokens = StringUtils.tokenize(wildcard, "/");
		ArrayList<File> files = new ArrayList<File>();
		getFilesFromWildcardRecursively(wildcardTokens, files, parentDirectory, 0);
		return files.toArray(new File[files.size()]);
	}

	private static void getFilesFromWildcardRecursively(String[] wildcardTokens,
			final ArrayList<File> files, File parentDirectory, int depth) throws IOException {

		if (!parentDirectory.isDirectory()) {
			throw new IOException("Not a valid directory: " + parentDirectory.getAbsolutePath());
		}

		final Pattern pattern = Pattern.compile(wildcardTokens[depth]);
		final String currentToken = wildcardTokens[depth];

		if (depth == wildcardTokens.length - 1) {
			// this is the farthest node we care about
			// so start adding files

			final FilenameFilter filter = new FilenameFilter() {

				public boolean accept(File dir, String name) {
					File file = new File(dir, name);
					if (file.isFile()) {
						final Matcher matcher = pattern.matcher(name);
						return matcher.matches();
					} else {
						return false;
					}
				}
			};

			File[] results = parentDirectory.listFiles(filter);

			for (final File result : results) {
				files.add(result);
			}

		} else {
			// this is a directory, keep recursing
			final FilenameFilter filter = new FilenameFilter() {

				public boolean accept(File dir, String name) {
					File file = new File(dir, name);
					if (file.isDirectory()) {
						final Matcher matcher = pattern.matcher(name);
						return matcher.matches();
					} else {
						return false;
					}
				}
			};

			// don't do any searching unless we have to
			if (currentToken.contains("*") || currentToken.contains(".")) {
				// this is a wilcard
				File[] results = parentDirectory.listFiles(filter);
				if (results == null)
					throw new RuntimeException(
							"null list for results. Either an IO error occurred or the following path is not a directory:"
									+ parentDirectory.getAbsolutePath());

				for (final File result : results) {
					getFilesFromWildcardRecursively(wildcardTokens, files, result, depth + 1);
				}
			} else {
				// this is a literal
				File result = new File(parentDirectory, currentToken);
				getFilesFromWildcardRecursively(wildcardTokens, files, result, depth + 1);
			}
		}

	}

	public static File[] getNormalFiles(final File root) {
		assert root != null : "root must not be null";

		final FileFilter filter = new FileFilter() {

			public boolean accept(File f) {
				return !f.isDirectory();
			}
		};

		File[] files = root.listFiles(filter);
		return files;
	}

	public static File[] getSubdirectories(final File root) throws IOException {
		assert root != null : "root must not be null";

		final FileFilter filter = new FileFilter() {

			public boolean accept(File f) {
				return f.isDirectory();
			}
		};

		File[] files = root.listFiles(filter);
		if(files == null)
		    throw new IOException("IO Error or not a valid path: " + root.getAbsolutePath());
		return files;
	}

	public static void saveTextFileFromStream(final File file, final InputStream inStream)
			throws IOException {
		file.createNewFile();
		final BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
		final PrintWriter out = new PrintWriter(file);

		String line = in.readLine();
		while (line != null) {
			out.write(line);
			line = in.readLine();
		}
		out.close();
		in.close();
	}

	public static void saveFileFromString(final File file, final String str) throws IOException {
		file.createNewFile();
		final PrintWriter out = new PrintWriter(file);
		out.println(str);
		out.close();
	}

	public static String getFileAsString(final File file) throws IOException {
		final StringBuilder builder = new StringBuilder(100000);
		final BufferedReader in = new BufferedReader(new FileReader(file));

		String line;
		while ((line = in.readLine()) != null) {
			builder.append(line + "\n");
		}
		in.close();

		return builder.toString();
	}

	public static void addLinesOfFileToCollection(final File file, Collection<String> col)
			throws IOException {
		final BufferedReader in = new BufferedReader(new FileReader(file));

		String line;
		while ((line = in.readLine()) != null) {
			col.add(line);
		}
		in.close();
	}

	/**
	 * Returns a new file in the same directory as the original, but with a new extension
	 * 
	 * @param file
	 * @param newExt
	 * @return
	 */
	public static File changeFileExt(File file, String newExt) {
		String newName = StringUtils.substringBefore(file.getName(), ".") + newExt;
		return new File(file.getParentFile(), newName);
	}
	
	/**
	 * Returns a new file with the same name as the original, but in a different directory
	 * 
	 * @param file
	 * @param newDir
	 * @return
	 */
	public static File changeFileDir(File file, File newDir) {
		return new File(newDir, file.getName());
	}

	public static void main(String... args) throws Exception {
		File[] files = getFilesFromWildcard("/media/disk/research/corpora/jpen/jp/*/*.txt");
		System.out.println("Found " + files.length + " files");
	}
}
