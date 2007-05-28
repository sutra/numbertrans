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
package info.jonclark.corpus.util;

import info.jonclark.corpus.CorpusUtils;
import info.jonclark.corpus.lm.LanguageModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 * A convenient way of building sentences out of an input stream. Keeps
 * sentences together, even if they are split by line breaks.
 * <p>
 * The typical usage is to call <code>nextSentence()</code> until no more
 * sentences remain.
 */
public class SentenceTokenizer {

    private final BufferedReader in;
    private List<String> tokens = new LinkedList<String>();
    private final boolean addSentenceBoundaryMarkers;
    private final boolean useNewLineAsSentenceDelim;

    // state variables
    private boolean hasMoreSentences = true;
    private int nSentenceEndIndex;
    private String prevLine;

    /**
         * @param inStream
         * @param addSentenceBoundaryMarkers
         *                Add <BOS> and <EOS> markers for use with language
         *                modeling.
         * @throws IOException
         */
    public SentenceTokenizer(InputStream inStream, boolean useNewLineAsSentenceDelim,
	    boolean addSentenceBoundaryMarkers) throws IOException {
	in = new BufferedReader(new InputStreamReader(inStream));
	this.addSentenceBoundaryMarkers = addSentenceBoundaryMarkers;
	this.useNewLineAsSentenceDelim = useNewLineAsSentenceDelim;

	searchForNext();
    }

    public boolean hasNextSentence() {
	return hasMoreSentences;
    }

    private int indexOfSentenceDelim() {
	int i = 0;
	for (final String token : tokens) {
	    if (token.length() == 1 && CorpusUtils.SENTENCE_BOUNDARY_DELIMS.contains(token)) {
		return i;
	    }
	    i++;
	}

	return -1;
    }

    private void searchForNext() throws IOException {
	prevLine = "";
	nSentenceEndIndex = -1;

	if (useNewLineAsSentenceDelim) {
	    // only read in a maximum of one line (if necessary)
	    if ((nSentenceEndIndex = indexOfSentenceDelim()) == -1
		    && (prevLine = in.readLine()) != null) {
		tokens.addAll(CorpusUtils.tokenize(prevLine));
	    }
	} else {
	    // keep reading in lines until we get a sentence delim
	    while ((nSentenceEndIndex = indexOfSentenceDelim()) == -1
		    && (prevLine = in.readLine()) != null) {
		// append the next line's tokens to the token list
		tokens.addAll(CorpusUtils.tokenize(prevLine));
	    }
	}

	if (prevLine == null && nSentenceEndIndex == -1) {
	    hasMoreSentences = false;
	}
    }

    public List<String> nextSentence() throws IOException {
	if (!hasMoreSentences)
	    throw new RuntimeException("No sentences remaining.");

	final List<String> sentence;

	if (prevLine == null && nSentenceEndIndex == -1) {
	    hasMoreSentences = false;
	    sentence = tokens;
	} else if (nSentenceEndIndex == -1) {
	    assert useNewLineAsSentenceDelim;
	    sentence = tokens;
	    tokens = new LinkedList<String>();
	} else {
	    sentence = tokens.subList(0, nSentenceEndIndex + 1);
	    tokens = tokens.subList(nSentenceEndIndex + 1, tokens.size());
	}

	if (addSentenceBoundaryMarkers) {
	    addBoundaryMarkers(sentence);
	}

	searchForNext();

	return sentence;
    }

    /**
         * Add <BOS> and <EOS> markers for use with language modeling.
         * 
         * @param sentence
         */
    public static void addBoundaryMarkers(List<String> sentence) {
	sentence.add(0, LanguageModel.BEGIN_SENTENCE);
	sentence.add(LanguageModel.END_SENTENCE);
    }
}
