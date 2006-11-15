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

import info.jonclark.util.StringUtils;

/**
 * Utilities useful in working with corpus linguistics
 */
public class CorpusUtils {
    
    private static final String SENTENCE_BOUNDARY_DELIMS = ".!?";
    
    /**
     * Counts the number of words of an untokenized string.
     * If your string is already tokenized, consider
     * using <code>StringUtils.countTokens()</code>
     * 
     * Currently, only calls <code>StringUtils.countTokens()</code>.
     * Eventually, needs to use a linguistically accurate
     * tokenizer before counting tokens.
     * 
     * @param str
     * @return
     */
    public static int countWords(final String str) {
        // TODO: Make this word count smarter so that
        // it does linguistically-accurate word counts.
        
        // I guess this means we have to write a tokenizer
        // ... and then something that distinguishes tokens?
        
        return StringUtils.countTokens(str);
    }
    
    /**
     * Count the number of sentence boundary markers in a given sentence.
     * Currently counts any mataching character. Ultimately needs
     * to detect cases such as abbreviations.
     * 
     * @param str
     * @return The number of sentence boundaries in <code>str</code>
     */
    public static int countSentenceBoundaries(final String str) {
        return StringUtils.countOccurancesOfAnyDelim(str, SENTENCE_BOUNDARY_DELIMS);
    }
    
    /**
     * Currently tokenizes only by spaces. Eventually needs to tokenize
     * with a more linguistically informed method.
     * 
     * @param str
     * @return
     */
    public static String[] tokenize(final String str) {
	return StringUtils.tokenize(str);
    }
}
