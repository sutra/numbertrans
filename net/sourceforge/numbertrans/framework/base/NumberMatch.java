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

import info.jonclark.lang.IntRange;
import info.jonclark.lang.TokenArray;

/**
 * Stores information about the number match that was found including the
 * matching tokens. These are stored as an immutable token array to prevent
 * problems with tokens being modified unexpectedly.
 */
public class NumberMatch {
    private final IntRange range;
    private final TokenArray matchingTokens;
    private final GeneralNumber.Context context;

    /**
         * Create a new NumberMatch object.
         * 
         * @param range
         *                The zero-based range of tokens that matched (according
         *                to the original array in which they were found).
         * @param sourceTokens
         *                The original array in which the match was found.
         * @param context
         *                The context in which this number was found. (e.g.
         *                Cardinal, Ordinal, Decimal)
         */
    public NumberMatch(final IntRange range, final String[] sourceTokens,
	    final GeneralNumber.Context context) {
	this.range = range;
	this.context = context;
	matchingTokens = new TokenArray(sourceTokens, range);
    }

    /**
         * Get the context in which this number was found. (e.g. Cardinal,
         * Ordinal, Decimal)
         */
    public GeneralNumber.Context getContext() {
	return context;
    }

    /**
         * Get the zero-based matching range of tokens (according to the
         * original array in which they were found).
         */
    public IntRange getRange() {
	return range;
    }

    /**
         * Gets the matching tokens.
         */
    public TokenArray getMatchingTokens() {
	return matchingTokens;
    }
}
