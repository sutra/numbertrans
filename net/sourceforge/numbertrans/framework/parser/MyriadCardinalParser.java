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

package net.sourceforge.numbertrans.framework.parser;

import java.util.Hashtable;

import net.sourceforge.numbertrans.framework.base.GeneralNumber;
import net.sourceforge.numbertrans.framework.base.NumberMatch;
import net.sourceforge.numbertrans.framework.base.WholeNumber;

import info.jonclark.lang.TokenArray;

/**
 * A parser for numbers that use the myriad system (i.e. grouping by 10,000
 * instead of 1000). Examples include Chinese, Japanese, and Korean.
 */
public class MyriadCardinalParser extends NumberParser<WholeNumber> {

    protected final Hashtable<Character, Long> values = new Hashtable<Character, Long>();

    /**
         * Used to indicate that a character should be skipped by the parsing
         * algorithm. This is usually due to the character ALWAYS being
         * encountered with a previous character. Thus, the value of the current
         * digit has already been uniquely identified when this digit is
         * encountered. e.g. The Native Korean number 1: 하나
         */
    public static final long CHARACTER_VALUE_SKIP = Long.MIN_VALUE;

    // TODO: Share hashtables with same parameters as static objects

    public long getCharacterValue(final char c) {
	if (c >= '0' && c <= '9') {
	    return c - '0';
	} else {
	    final Long value = values.get(c);
	    assert value != null;
	    return value;
	}
    }

    private long toLong(final String sourceNumber) {
	long localSum = 0; // nodes of the number "tree"; most local value
	long localProduct = 0; // middle node of the number "tree"
	long localMultiplier = 0;
	long totalValue = 0; // top node of the number "tree"

	for (int i = 0; i < sourceNumber.length(); i++) {
	    final char c = sourceNumber.charAt(i);
	    final long charValue = getCharacterValue(c);

	    if (charValue == CHARACTER_VALUE_SKIP) {
		continue;
	    } else if (charValue < 10) {

		// this is a ones digit, it only gains value
		// by virtue of its position (base-10 positional)
		localSum *= 10;
		localSum += charValue;

	    } else {
		if (charValue > localMultiplier) {

		    // this is a localMultiplier
		    // we will continue accumulating value in the
		    // localProduct
		    // and reset the localSum
		    localMultiplier = charValue;
		    localProduct = (localProduct + localSum) * localMultiplier;
		    localSum = 0;

		} else {

		    // this is a global multiplier
		    // dump the localProduct to the totalValue
		    // reset localProduct and localSum
		    localMultiplier = charValue;
		    totalValue += localProduct + localSum * localMultiplier;
		    localSum = 0;
		    localProduct = 0;

		}
	    } // end if charValue < 10
	} // end for digits

	// flush the last digits
	totalValue += localSum;
	totalValue += localProduct;

	return totalValue;
    } // end toLong()

    @Override
    /**
         * Be very careful with this method. Calling it assumes that you already
         * know that the supplied number is a cardinal. If not, a
         * <code>NumberFormatException</code> will be thrown. If you are
         * unsure of the context of a number, it is recommended that you use the
         * <code>NumberFinder</code> first.
         */
    public WholeNumber getNumberFromString(String strNumber) throws NumberFormatException {
	// TODO: Determine whether the number is a decimal, whole number, or
	// fractional number

	// TODO: Do some assertations to make sure we have the right type of
	// number here

	final long value = toLong(strNumber);
	final int nLeadingZeros = countLeadingZeros(strNumber);
	return new WholeNumber(value, nLeadingZeros, GeneralNumber.Context.CARDINAL);
    }

    @Override
    public WholeNumber getNumberFromFind(NumberMatch find) {
	// We don't have to do as much checking here since we're certain of the
	// context from the find.
	assert find.getContext() == GeneralNumber.Context.CARDINAL;

	final TokenArray arr = find.getMatchingTokens();
	assert arr.length() == 1 : "More than one matching token for cardinal number";
	final String strNumber = arr.tokenAt(0);

	final long value = toLong(strNumber);
	final int nLeadingZeros = countLeadingZeros(strNumber);

	return new WholeNumber(value, nLeadingZeros, find.getContext());
    }
}
