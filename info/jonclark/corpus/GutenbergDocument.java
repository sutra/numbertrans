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

import info.jonclark.util.*;

import java.io.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.*;

/**
 * @author Jonathan
 */
public class GutenbergDocument extends CorpusDocument {

    private final UniqueWordCounter documentWordCount = new UniqueWordCounter(true);
    private final UniqueWordCounter globalWordCount;
    private long nSentences = 0;

    // line numbers on which the "real" text (not legal junk) begins/ends
    private int nTextBegin = 0;
    private int nTextEnd = 0;

    // TODO: We must associate this document with some stream or file that
    // can be recalled at will, right?
    // Would it be acceptable to tie this to a disk file and just require
    // that the file be cached there? YES.

    // the maximum number of lines spanned by the legal mumbo jumbo
    // before and after the "real" text
    private int nMaxLinesBefore = 0;
    private int nMaxLinesAfter = 0;

    private static HashSet<String> beforeStrings;
    private static HashSet<String> afterStrings;
    private static String beforeStringsFile;
    private static String afterStringsFile;
    private static boolean initialized = false;

    private final static int SUBSTRING_LEN = 20;
    private String friendlyTitle;
    private final boolean keepDates;
    private static final int MAX_DIGITS_AFTER_COMMA = 2;
    private final Logger log;

    /**
         * Required properties: keepAuthorDates, startStringsFile,
         * endStringsFile
         * 
         * @param log
         * @param props
         * @param globalWordCount
         *                A word counter for this entire corpus (can be null).
         *                NOTE: You can assume that all inputs for this word
         *                counter WILL be interned. (It can be created with
         *                <code>new UniqueWordCounter(true)</code>.
         * @throws IOException
         */
    public GutenbergDocument(Logger log, Properties props, UniqueWordCounter globalWordCount)
	    throws PropertiesException, IOException {
	String[] requiredProperties = new String[] { "keepAuthorDates", "beforeStringsFile",
		"afterStringsFile" };

	PropertyUtils.validateProperties(props, requiredProperties);

	// put these properties values in a constants file

	this.keepDates = Boolean.parseBoolean(props.getProperty("keepAuthorDates"));
	this.log = log;
	this.globalWordCount = globalWordCount;

	final String beforeStringsFile = props.getProperty("beforeStringsFile");
	final String afterStringsFile = props.getProperty("afterStringsFile");

	initialize(beforeStringsFile, afterStringsFile);
    }

    /**
         * Initialize this class's static fields from a serialized file.
         * 
         * @param beforeStringsFile
         * @param afterStringsFile
         * @throws FileNotFoundException
         * @throws IOException
         */
    private void initialize(final String beforeStringsFile, final String afterStringsFile)
	    throws FileNotFoundException, IOException {
	// NOTE: First line of file must be the max before/after lines and the
	// second must be the number of lines contained in the file

	// make sure we didn't hurt anything with our jerry-rigged static
	// initialization
	// TODO: Create a static string file manager that shares objects between
	// instances
	if (!beforeStringsFile.equals(GutenbergDocument.beforeStringsFile)
		|| !afterStringsFile.equals(GutenbergDocument.afterStringsFile)) {
	    throw new RuntimeException(
		    "Non-matching strings file for already initialized string set");
	}

	if (!initialized) {
	    // before strings
	    BufferedReader in = new BufferedReader(new FileReader(beforeStringsFile));
	    nMaxLinesBefore = Integer.parseInt(StringUtils.substringAfter(in.readLine(),
		    "maxLinesBefore = "));
	    final int nBeforeStrings = Integer.parseInt(StringUtils.substringAfter(in.readLine(),
		    "linesInFile = "));

	    beforeStrings = new HashSet<String>(nBeforeStrings);
	    for (String line = in.readLine(); line != null; line = in.readLine()) {
		if (!line.startsWith("#") && line.length() != 0)
		    beforeStrings.add(line.substring(0, SUBSTRING_LEN));
	    }

	    // after strings
	    in = new BufferedReader(new FileReader(afterStringsFile));
	    nMaxLinesAfter = Integer.parseInt(StringUtils.substringAfter(in.readLine(),
		    "maxLinesAfter = "));
	    final int nAfterStrings = Integer.parseInt(StringUtils.substringAfter(in.readLine(),
		    "linesInFile = "));
	    afterStrings = new HashSet<String>(nAfterStrings);
	    for (String line = in.readLine(); line != null; line = in.readLine()) {
		if (!line.startsWith("#") && line.length() != 0)
		    afterStrings.add(line.substring(0, SUBSTRING_LEN));
	    }

	    initialized = true;
	}
    }

    public void save(final String beforeStringsFile, final String afterStringsFile)
	    throws IOException {
	PrintWriter out = new PrintWriter(new FileWriter(beforeStringsFile));
	out.println("maxLinesBefore = " + nMaxLinesBefore);
	out.println("linesInFile = " + afterStrings.size());
	for (String s : beforeStrings)
	    out.println(s);
	out.close();

	out = new PrintWriter(new FileWriter(afterStringsFile));
	out.println("maxLinesAfter = " + nMaxLinesAfter);
	out.println("linesInFile = " + afterStrings.size());
	for (String s : afterStrings)
	    out.println(s);
	out.close();
    }

    /**
         * Reads in a Guttenberg author format, stripping off author dates if
         * necessary
         * 
         * @param author
         *                The author to set.
         */
    public void setAuthor(String author) {
	// if string after last comma has
	// more than 3 digits, chop it
	int nDigitCount = 0;
	if (!keepDates) {
	    final int nLastComma = author.lastIndexOf(',');
	    if (nLastComma > -1) {
		final int len = author.length();
		for (int i = nLastComma + 1; i < len && nDigitCount <= MAX_DIGITS_AFTER_COMMA; i++) {
		    if (Character.isDigit(author.charAt(i)))
			nDigitCount++;
		}
		if (nDigitCount > MAX_DIGITS_AFTER_COMMA) {
		    super.setAuthor(author.substring(0, nLastComma));
		}
	    }
	}
	if (nDigitCount <= MAX_DIGITS_AFTER_COMMA) {
	    super.setAuthor(author);
	}
    }

    /**
         * @return Returns the friendlyTitle.
         */
    public String getFriendlyTitle() {
	return friendlyTitle;
    }

    /**
         * @param friendlyTitle
         *                The friendlyTitle to set.
         */
    public void setFriendlyTitle(String friendlyTitle) {
	this.friendlyTitle = friendlyTitle;
	if (getTitle().equals("")) {
	    // extract title from friendly title
	    setTitle(StringUtils.substringBefore(friendlyTitle, " by "));
	}
	if (getAuthor().equals("")) {
	    // extract title from friendly title
	    setAuthor(StringUtils.substringAfter(friendlyTitle, " by "));
	}
    }

    /*
         * (non-Javadoc)
         * 
         * @see info.jonclark.corpus.CorpusDocument#parse(java.io.InputStream)
         */
    public void parse(File file) throws IOException {
	findLegalTextBoundaries(file);
	calculateStatistics(file);
    }

    /**
         * Use heuristics and machine learning to find the beginning and end of
         * this Project Gutenberg eText's legal stuff (occurs at top and bottom
         * of file).
         * 
         * @param file
         * @throws IOException
         */
    public void findLegalTextBoundaries(File file) throws IOException {
	final BufferedReader in = new BufferedReader(new FileReader(file));

	int nLine = 0;
	int nCurrentTextStart = 0;
	int nCurrentTextEnd = 0;
	int nCurrentLinesAfter = 0;

	for (String line = in.readLine(); line != null; line = in.readLine()) {
	    nLine++;

	    // Here, we use substring because sometimes we strip the end of
	    // the line off because it contains eText-specific material
	    // ...plus it's slightly faster
	    line = line.substring(0, SUBSTRING_LEN);

	    // The algorithm:
	    //
	    // 1. Finding the beginning:
	    // a. If current line matches the beginning of a stringsBefore
	    // _____ line, mark it as a "canidate" (C)
	    // b.
	    
	    
	    // 2. Finding the end:
	    // XXX What if we find a line that's in both the before AND after strings?
	    // do we want to list lines that belong to ONLY one or the other? (see below)

	    // final String strSmallPrintBegin = "***START**THE SMALL
	    // PRINT!";
	    // final String strSmallPrintEnd = "*END*THE SMALL PRINT";

	} // end while line
    }

    /**
         * Do word and sentence counts on this eText.
         * 
         * @param file
         * @throws IOException
         */
    public void calculateStatistics(File file) throws IOException {
	final BufferedReader in = new BufferedReader(new FileReader(file));

	int nLine = 0;
	for (String line = in.readLine(); line != null; line = in.readLine()) {
	    nLine++;

	    if (nLine >= nTextBegin && nLine <= nTextEnd) {
		String[] tokens = CorpusUtils.tokenize(line);

		// remember that we promised unique word counter interned
		// strings
		StringUtils.internTokens(tokens);
		nSentences += CorpusUtils.countSentenceBoundaries(line);

		// TODO: this will NOT give an accurate word count due to
		// periods, etc.
		for (String word : tokens)
		    documentWordCount.addWord(word);

		if (globalWordCount != null) {
		    for (String word : tokens)
			globalWordCount.addWord(word);
		}
	    }
	} // end for lines

    }

    /*
         * (non-Javadoc)
         * 
         * @see info.jonclark.corpus.interfaces.CorpusStatistics#getUniqueWordcount()
         */
    public long getUniqueWordcount() {
	return documentWordCount.getUniqueWordCount();
    }

    public long getWordcount() {
	return documentWordCount.getNonuniqueWordCount();
    }

    public float getMeanLengthOfSentence() {
	if (nSentences != 0)
	    return (float) getWordcount() / (float) nSentences;
	else
	    return 0;
    }

    public static void main(String[] args) throws Exception {
	// Properties props = PropertyUtils.getProperties("");
	// GutenbergDocument doc = new
	// GutenbergDocument(Logger.getLogger("gutenberg"), props);
	// InputStream stream = WebUtils
	// .getUrlStream("http://www.gutenberg.org/dirs/etext06/7lrf110.zip");
	// BufferedZipInputStream in = new BufferedZipInputStream(stream);
	// in.nextEntry();
	// doc.parse(in);
    }
}
