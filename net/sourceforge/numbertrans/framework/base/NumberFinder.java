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
package net.sourceforge.numbertrans.framework.base;

import info.jonclark.lang.LongRange;

/**
 * An abstract class to examines a string in some source language and identifies
 * numbers. Finders for all languages should inherit from this class.
 * <p>
 * This class carries the burden not only of identifying numbers, but also
 * tagging their context.
 * <p>
 * Note that many languages contain numbers that are ambiguous; that is, that
 * cannot be positively identified as just numbers and may have some other
 * semantic meaning. This class provides safety thresholds so that the user may
 * choose how aggressive to be in tuning the precision versus recall of this
 * class.
 */
public abstract class NumberFinder {

    private final Retokenizer tokenizer;

    /**
         * Numbers that should not be considered numbers by this finder.
         * Example: 1-3 in Chinese since these can be parts of pronouns.
         */
    protected final LongRange[] unsafeNumbers;

    public NumberFinder(final LongRange[] unsafeNumbers, final Retokenizer tokenizer) {
	this.unsafeNumbers = unsafeNumbers;
	this.tokenizer = tokenizer;
    }

    /**
         * Determine if this number occurs in the list of "unsafe" numbers
         * 
         * @param number
         *                The whole number to be tested
         * @return True if this number should NOT be identified as a number
         */
    public boolean isUnsafeNumber(WholeNumber number) {
	for (int i = 0; i < unsafeNumbers.length; i++)
	    if (unsafeNumbers[i].isInRange(number.getValue()))
		return true;
	return false;
    }

    /**
         * Get a tokenizer/retokenizer that will produce an array of tokens
         * compatible with this <code>NumberFinder</code>
         */
    public Retokenizer getTokenizer() {
	return tokenizer;
    }

    /**
         * A convenience method that starts finding from the beginning of the
         * token array. See the documentation for
         * <code>nextMatch(String, int)</code> for more details.
         * 
         * @param tokens The array of tokens to examine
         * @return 
         */
    public NumberMatch nextMatch(final String[] tokens) {
	return nextMatch(tokens, 0);
    }

    public abstract NumberMatch nextMatch(final String[] tokens, int nBeginIndex);
}
