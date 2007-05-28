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
package info.jonclark.corpus.lm;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import info.jonclark.corpus.UniqueWordCounter;
import info.jonclark.corpus.lm.smoothing.LanguageModelSmoothing;
import info.jonclark.corpus.util.SentenceTokenizer;
import info.jonclark.lang.NaiveSuffixArray;
import info.jonclark.lang.TokenArray;
import info.jonclark.log.LogUtils;
import info.jonclark.log.VarLogger;
import info.jonclark.stat.SecondTimer;

/**
 * A Language Model implemented using a Suffix Array. This is essentially a
 * n-gram language model with infinite n. That is, the length of n is only
 * constrained by the length of the training corpus.
 * <p>
 * Current calculation is performed by an unsmoothed n-gram language model.
 * <p>
 * Why use a TokenArray instead of a String[]? Because it will save memory and
 * it's safer.
 */
public class SuffixArrayLanguageModel implements LanguageModel {
    // TODO: Cache n-grams

    private static final int DEFAULT_SENTENCE_COUNT = 100000;

    private final NaiveSuffixArray suffixArray;
    private LanguageModelSmoothing smoothing;
    private UniqueWordCounter wordCounter = new UniqueWordCounter(false, false);
    private static final VarLogger log = LogUtils.getLogger();
    private final int nGramMax;
    private final int nTokenCount;

    /**
         * Create a language model from an untokenized text file.
         * 
         * @param inStream
         * @param smoother
         *                The smoothing technique to be used (enumerated in
         *                LanguageModel.SmoothingTechnique)
         * @param nGramMax
         *                The maximum order of n-gram that will be matched. For
         *                example, a 4-gram model would pass 4 here.
         * @throws IOException
         */
    public SuffixArrayLanguageModel(InputStream inStream, LanguageModelSmoothing smoother,
	    int nGramMax, boolean useNewLineAsSentenceDelim) throws IOException {
	this.nGramMax = nGramMax;
	setSmoothingTechnique(smoother);
	
	SentenceTokenizer sentenceBuilder = new SentenceTokenizer(inStream,
		useNewLineAsSentenceDelim, false);
	ArrayList<TokenArray> sentences = new ArrayList<TokenArray>(DEFAULT_SENTENCE_COUNT);
	while (sentenceBuilder.hasNextSentence()) {
	    List<String> sentence = sentenceBuilder.nextSentence();
	    wordCounter.addWords(sentence);
	    SentenceTokenizer.addBoundaryMarkers(sentence);
	    sentences.add(new TokenArray(sentence));
	}
	wordCounter.freezeCounts();

	log.info("Building Suffix array...");
	SecondTimer timer = new SecondTimer(true, true);
	suffixArray = new NaiveSuffixArray(sentences);
	log.info("Constructed suffix array in {0} seconds", timer.getSecondsFormatted());
	this.nTokenCount = suffixArray.size();

	assert nGramMax > 1 : "This Language Model does not support unigrams";
    }

    /**
         * Count the number of times a given string of tokens occurred in the
         * language model.
         * 
         * @param phrase
         */
    public int countOccurances(TokenArray phrase) {
	return suffixArray.countOccurances(phrase);
    }

    /**
         * Get the score (probability) for a sentence (array of tokens). The
         * sentence must begin with the token LanguageModel.START_SENTENCE and
         * end with the token LanguageModel.END_SENTENCE.
         */
    public double getSentenceProbability(TokenArray sentence) {
	return smoothing.getSentenceProbability(sentence);
    }

    /**
         * Get the probability of a word, given the word and its context. The
         * probability of last token (n) in the phrase will be evaluated while
         * tokens 0 through n-1 will be used as context.
         * 
         * @param phrase
         * @return
         */
    public double getWordProbability(TokenArray phrase) {
	return smoothing.getWordProbability(phrase);
    }

    /**
         * Set the smoothing technique used to calculate probability values.
         * This method allows for changing the method on-the-fly so that
         * multiple methods can be evaluated without the need for the expensive
         * rebuilding of underlying data tables.
         * 
         * @param smoothingTechnique
         */
    public void setSmoothingTechnique(LanguageModelSmoothing smoothingTechnique) {
	this.smoothing = smoothingTechnique;
	smoothing.setParent(this);
    }

    public int getTokenCount() {
	return nTokenCount;
    }

    public int getNGramMax() {
	return nGramMax;
    }

    public int getVocabularySize() {
	// We don't support more than 2 billion words
	return (int) wordCounter.getUniqueWordCount();
    }
}
