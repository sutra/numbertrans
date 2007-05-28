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
package info.jonclark.corpus;

import java.io.File;
import java.io.PrintWriter;

import info.jonclark.io.FileLineArray;
import info.jonclark.io.FileLineArray.Mode;
import info.jonclark.util.ArrayUtils;
import info.jonclark.util.StringUtils;

/**
 * Split a single text file into multiple pieces. For example, a book might be
 * split into n-folds (n even pieces) for research evaluation.
 * <p>
 * NOTE: This implementation has not been optimized for extremely large files
 */
public class Splitter {
    public static void main(String[] args) throws Exception {
	if (args.length < 1) {
	    System.err.println("Usage: program <file_to_split> [options]");
	    System.err.println("-n <x> = specify that the text should be split into <x> files");
	    System.err.println("-a = Fill files by distributing lines in a round-robin fashion");
	    System.err.println("-c = Split into contiguous pieces");
	    System.err.println("-e <x> <y> = Use <y> as the suffix (instead of the current suffix) for <x> files");
	    System.exit(1);
	}

	int nIndex = ArrayUtils.findInUnsortedArray(args, "-n") + 1;
	final int nFiles = Integer.parseInt(args[nIndex]);

	// prepare input file
	String inputFilename = args[0];
	FileLineArray in = new FileLineArray(new File(inputFilename), Mode.READ);
	final int nLines = in.getLineCount();

	// prepare output files
	PrintWriter[] outs = new PrintWriter[nFiles];
	if (ArrayUtils.unsortedArrayContains(args, "-e")) {
	    // use custom suffixes
	    nIndex = ArrayUtils.findInUnsortedArray(args, "-e");
	    int nCurrentFile = 0;
	    while (nIndex != -1) {

		int nFilesWithSuffix = Integer.parseInt(args[nIndex + 1]);
		String prefix = StringUtils.substringBefore(inputFilename, ".");
		String suffix = args[nIndex + 2];

		System.out.println("Using " + suffix + " as suffix for " + nFilesWithSuffix
			+ " files.");

		for (int i = 0; i < nFilesWithSuffix && nCurrentFile < nFiles; nCurrentFile++, i++) {
		    String outputFilename = prefix + "_" + (i + 1) + "." + suffix;
		    outs[nCurrentFile] = new PrintWriter(outputFilename);
		}

		nIndex = ArrayUtils.findInUnsortedArray(args, "-e", nIndex + 3);
	    }

	    String prefix = StringUtils.substringBefore(inputFilename, ".");
	    String suffix = StringUtils.substringAfter(inputFilename, ".");
	    for (int i = 0; nCurrentFile < nFiles; nCurrentFile++, i++) {
		String outputFilename = prefix + "_" + (i + 1) + "." + suffix;
		outs[nCurrentFile] = new PrintWriter(outputFilename);
	    }
	} else {
	    // use default suffixes
	    System.out.println("Preserving file prefixes.");
	    String prefix = StringUtils.substringBefore(inputFilename, ".");
	    String suffix = StringUtils.substringAfter(inputFilename, ".");
	    for (int i = 0; i < nFiles; i++) {
		String outputFilename = prefix + "_" + (i + 1) + "." + suffix;
		outs[i] = new PrintWriter(outputFilename);
	    }
	}

	if (ArrayUtils.unsortedArrayContains(args, "-a")) {
	    // alternate lines
	    System.out.println("Separating " + inputFilename + " into " + nFiles
		    + " alternating pieces");

	    int nCurrentFile = 0;
	    for (int i = 0; i < nLines; i++) {
		String line = in.readLine();
		assert line != null : "Encountered null for line";
		outs[nCurrentFile].println(line);
		nCurrentFile++;
		nCurrentFile %= nFiles;
	    }

	} else if (ArrayUtils.unsortedArrayContains(args, "-c")) {
	    // contiguous pieces
	    System.out.println("Separating " + inputFilename + " into " + nFiles
		    + " contiguous pieces");

	    final int nLinesPerFile = in.getLineCount() / nFiles;

	    // handle all but last file
	    for (int i = 0; i < nFiles - 1; i++) {
		for (int j = 0; j < nLinesPerFile; j++) {
		    String line = in.readLine();
		    assert line != null : "Encountered null for line";
		    outs[i].println(line);
		}
	    }

	    // handle last file -- put all remaining lines here
	    String line = null;
	    while ((line = in.readLine()) != null) {
		outs[nFiles - 1].println(line);
	    }

	}

	// close all files
	for (int i = 0; i < nFiles; i++) {
	    outs[i].flush();
	    outs[i].close();
	}
    }
}
