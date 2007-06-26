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
package info.jonclark.corpus.tokenize;

import info.jonclark.properties.PropertyUtils;
import info.jonclark.util.FileUtils;
import info.jonclark.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;

/**
 * A NLP tokenizer for the English language
 */
public class EnglishTokenizer implements Tokenizer {

    private final HashSet<String> abbreviations = new HashSet<String>();

    public static final String AMBIGUOUS_PUNCTUATION = ".,";

    public static final String SENTENCE_PUNCTUATION = "!?\n";
    public static final String NORMAL_PUNCTUATION = SENTENCE_PUNCTUATION + ";:\'\"\\/-$%()[]";
    public static final String UNAMBIGUOUS_PUNCTUATION = NORMAL_PUNCTUATION + "|@#^&*<>";

    private final boolean removeNewLines;
    private final boolean doLowercasing;

    public EnglishTokenizer(Properties props) throws IOException {

	final String abbrevFile = props.getProperty("abbrevFile");
	this.removeNewLines = Boolean.parseBoolean(props.getProperty("removeNewLines"));
	this.doLowercasing = Boolean.parseBoolean(props.getProperty("doLowercasing"));
	FileUtils.addLinesOfFileToCollection(new File(abbrevFile), abbreviations);

    }

    /**
         * Currently, a very cheesy way of tokenizing. Needs work to become
         * really effective
         * 
         * @param str
         * @return
         */
    public String[] tokenize(String str) {

	if (removeNewLines)
	    str = str.replace('\n', ' ');
	if (doLowercasing)
	    str = str.toLowerCase();

	// TODO: don't mangle abbreviations
	// TODO: handle URL's gracefully (e.g. google.com)
	// TODO: handle posessives and contractions nicely

	// modify this to use the new method, but keep the old so that we can
	// time it.
	for (int i = 0; i < UNAMBIGUOUS_PUNCTUATION.length(); i++) {
	    char c = UNAMBIGUOUS_PUNCTUATION.charAt(i);
	    str = StringUtils.replaceFast(str, "" + c, " " + c + " ");
	}

	String[] origTokens = StringUtils.tokenize(str);
	ArrayList<String> finalTokens = new ArrayList<String>(origTokens.length + 10);

	for (int i = 0; i < origTokens.length; i++) {
	    String currentToken = origTokens[i];

	    // deal with ambiguous punctuation
	    // we have to iterate through each character of the word to
                // handle
	    for (int j = 0; j < currentToken.length(); j++) {

		if (currentToken.charAt(j) == '.' && !belongsToNumber(currentToken, j)) {

		    String tokenBefore = currentToken.substring(0, j);
		    tokenBefore = tokenBefore.trim();
		    if (!tokenBefore.equals(""))
			finalTokens.add(tokenBefore);
		    finalTokens.add(".");
		    currentToken = currentToken.substring(j + 1);
		    j = 0;

		} else if (currentToken.charAt(j) == ',' && !belongsToNumber(currentToken, j)) {

		    String tokenBefore = currentToken.substring(0, j);
		    tokenBefore = tokenBefore.trim();
		    if (!tokenBefore.equals(""))
			finalTokens.add(tokenBefore);
		    finalTokens.add(",");
		    currentToken = currentToken.substring(j + 1);
		    j = 0;

		} else if (Character.isDigit(currentToken.charAt(j))) {
		    // if we meet both conditions, we'll handle the second
                        // on
		    // the next iteration
		    if (j > 0 && !isNumberPart(currentToken, j - 1)) {
			String tokenBefore = currentToken.substring(0, j);
			tokenBefore = tokenBefore.trim();
			if (!tokenBefore.equals(""))
			    finalTokens.add(tokenBefore);
			currentToken = currentToken.substring(j);
			j = 0;
		    } else if (j < currentToken.length() - 1 && !isNumberPart(currentToken, j + 1)) {
			String tokenBefore = currentToken.substring(0, j + 1);
			tokenBefore = tokenBefore.trim();
			if (!tokenBefore.equals(""))
			    finalTokens.add(tokenBefore);
			currentToken = currentToken.substring(j + 1);
			j = 0;
		    }
		}
	    }

	    currentToken = currentToken.trim();
	    if (!currentToken.equals(""))
		finalTokens.add(currentToken);
	}

	return finalTokens.toArray(new String[finalTokens.size()]);
    }

    private static boolean belongsToNumber(String str, int nIndex) {
	return nIndex >= 1 && Character.isDigit(str.charAt(nIndex - 1))
		&& nIndex < str.length() - 1 && Character.isDigit(str.charAt(nIndex + 1));
    }

    private static boolean isNumberPart(String str, int nIndex) {
	char c = str.charAt(nIndex);
	return Character.isDigit(c) || c == ',' || c == '.';
    }

    public static void main(String... args) throws Exception {
	if (args.length != 3) {
	    System.err.println("Usage: program <properties_file> <input_file_wildcard> <output_ext>");
	    System.exit(1);
	}

	Properties props = PropertyUtils.getProperties(args[0]);
	EnglishTokenizer etok = new EnglishTokenizer(props);
	SentenceTokenizer stok = new SentenceTokenizer(props);

	System.out.println("Finding files...");
	File[] files = FileUtils.getFilesFromWildcard(args[1]);
	System.out.println(files.length + " files found.");

	String outExt = args[2];

	for (final File file : files) {
	    String input = FileUtils.getFileAsString(file);

	    String[][] tokenized = stok.tokenizeToSentences(etok.tokenize(input));
	    String strTokenized = StringUtils.untokenize(tokenized, false);

	    String outName = StringUtils.substringBefore(file.getName(), ".") + outExt;
	    FileUtils.saveFileFromString(new File(file.getParentFile(), outName), strTokenized);
	}

	System.out.println("Done.");
    }
}
