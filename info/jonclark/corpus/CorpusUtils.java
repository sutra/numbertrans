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

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import info.jonclark.util.StringUtils;

/**
 * Utilities useful in working with corpus linguistics
 */
public class CorpusUtils {

    public static final String SENTENCE_BOUNDARY_DELIMS = ".!?";
    public static final String ALL_DELIMS = ":;,.?!/\\-_|()[]{}+=&\"'\t ";

    /**
         * Counts the number of words of a tokenized string.
         * 
         * @param tokens
         *                A list of tokens returned by
         *                <code>CorpusUtils.tokenize()</code>
         * @return
         */
    public static int countWords(final List<String> tokens) {
	int nWords = 0;

	for (final String token : tokens) {
	    if (!StringUtils.isComposedOf(token, ALL_DELIMS)) {
		nWords++;
	    }
	}

	return nWords;
    }

    /**
         * Removes all non-words from <code>tokens</code>.
         * 
         * @param tokens
         *                The list of tokens that will be altered such that it
         *                no longer contains non-words.
         */
    public static void filterNonWords(final List<String> tokens) {
	for (int i = 0; i < tokens.size(); i++) {
	    if (StringUtils.isComposedOf(tokens.get(i), ALL_DELIMS)) {
		tokens.remove(i);
	    }
	}
    }

    /**
         * Count the number of sentence boundary markers in a given sentence.
         * Currently counts any mataching character. Ultimately needs to detect
         * cases such as abbreviations.
         * 
         * @param str
         * @return The number of sentence boundaries in <code>str</code>
         */
    public static int countSentenceBoundaries(final List<String> tokens) {
	int nSentenceBoundaries = 0;

	for (final String token : tokens) {
	    if (token.length() == 1 && SENTENCE_BOUNDARY_DELIMS.contains(token)) {
		nSentenceBoundaries++;
	    }
	}

	return nSentenceBoundaries;
    }

    public static int countSentences(final List<String> tokens) {
	return countSentenceBoundaries(tokens) + 1;
    }

    /**
         * Tokenizes by all major punctuation. Does not give special treatment
         * to abbreviations.
         * 
         * @param str
         * @return
         */
    public static List<String> tokenize(final String str) {
	final LinkedList<String> list = new LinkedList<String>();
	final StringTokenizer tokenizer = new StringTokenizer(str, ALL_DELIMS, true);

	while (tokenizer.hasMoreTokens()) {
	    final String token = tokenizer.nextToken();
	    // don't add spaces
	    if (!token.equals(" ")) {
		list.add(token);
	    }
	}

	return list;
    }

    public static String untokenize(final String[] tokens) {
	StringBuilder builder = new StringBuilder();

	if (tokens.length > 0)
	    builder.append(tokens[0]);

	final String NO_SPACE_BEFORE = ".,!?'";
	final String NO_SPACE_AFTER = "'-";

	for (int i = 1; i < tokens.length; i++) {

	    if (NO_SPACE_BEFORE.contains(tokens[i])) {
		;
	    } else if (NO_SPACE_AFTER.contains(tokens[i - 1])) {
		;
	    } else {
		builder.append(" ");
	    }

	    builder.append(tokens[i]);
	}

	return builder.toString();
    }
}
