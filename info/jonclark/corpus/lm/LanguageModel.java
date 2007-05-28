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

import info.jonclark.lang.TokenArray;

public interface LanguageModel {

    public static final String BEGIN_SENTENCE = "<BOS>";
    public static final String END_SENTENCE = "<EOS>";

    /**
         * Count the number of times a given string of tokens occurred in the
         * language model.
         * 
         * @param phrase
         */
    public int countOccurances(TokenArray phrase);

    /**
         * Get the score (probability) for a sentence (array of tokens). The
         * sentence must begin with the token LanguageModel.START_SENTENCE and
         * end with the token LanguageModel.END_SENTENCE.
         * 
         * @param sentence
         *                The sentence whose probability is to be evaluated.
         */
    public double getSentenceProbability(TokenArray sentence);

    /**
         * Get the probability of a word, given the word and its context. The
         * probability of last token (n) in the phrase will be evaluated while
         * tokens 0 through n-1 will be used as context.
         * 
         * @param phrase
         * @return
         */
    public double getWordProbability(TokenArray phrase);

    /**
         * Get the number of tokens that were used to build this language model.
         * 
         * @return
         */
    public int getTokenCount();

    /**
         * Get the number of unique tokens that were used to build this language
         * model.
         * 
         * @return
         */
    public int getVocabularySize();

    /**
         * Get the maximum size of an n-gram for this language model. (For
         * example, this would return 3 for a trigram model).
         * 
         * @return
         */
    public int getNGramMax();
}
