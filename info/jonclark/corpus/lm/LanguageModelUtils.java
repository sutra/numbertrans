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

import java.util.ArrayList;

import info.jonclark.lang.TokenArray;
import info.jonclark.util.MathUtils;

/**
 * Utilities for handling Language Models. Most of these are measures for
 * evaluating LM performance. For example, the perplexity and cross-entropy
 * metrics.
 */
public class LanguageModelUtils {

    /**
         * Implemented from "An Empirical Study of Smoothing Techniques for
         * Language Modeling" by Stanley F. Chen (August 1998) p.8
         * 
         * @return
         */
    public static double calculateTextProbability(LanguageModel lm, ArrayList<TokenArray> sentences) {
	double prob = 1.0;
	for (final TokenArray sentence : sentences)
	    prob *= lm.getSentenceProbability(sentence);
	return prob;
    }

    /**
         * Implemented from "An Empirical Study of Smoothing Techniques for
         * Language Modeling" by Stanley F. Chen (August 1998) p.8
         * 
         * @return
         */
    public static double calculateCrossEntropy(LanguageModel lm, ArrayList<TokenArray> sentences) {
	long textLength = 0;
	for (final TokenArray sentence : sentences) {
	    // include the length of the <EOS> end of sentence token, but
	    // not <BOS> beginning of sentence token as per the footnote on
	    // p.8
	    textLength += sentence.length() - 1;
	}

	return -1 / textLength * MathUtils.log2(calculateTextProbability(lm, sentences));
    }

    /**
         * Implemented from "An Empirical Study of Smoothing Techniques for
         * Language Modeling" by Stanley F. Chen (August 1998) p.9
         * 
         * @return
         */
    public static double calculatePerplexity(LanguageModel lm, ArrayList<TokenArray> sentences) {
	return Math.pow(2.0, calculateCrossEntropy(lm, sentences));
    }
}
