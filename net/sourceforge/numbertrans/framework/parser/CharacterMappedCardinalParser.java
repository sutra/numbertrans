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

import net.sourceforge.numbertrans.framework.base.AbstractNumber;
import net.sourceforge.numbertrans.framework.base.NumberMatch;
import net.sourceforge.numbertrans.framework.base.WholeNumber;
import net.sourceforge.numbertrans.framework.parser.NumberParser;

/**
 * A class for converting out of number systems that have a 1-to-1 character
 * correspondence with the arabic numeral system.
 */
public class CharacterMappedCardinalParser extends NumberParser<WholeNumber> {

    private final char[] sourceDigits;

    /**
         * Create a new <code>CharacterMappedCardinalParser</code> object.
         * 
         * @param digits
         *                An array of length 10 containing the digits in the
         *                source language with the character for 0 as the zeroth
         *                element.
         */
    public CharacterMappedCardinalParser(char[] digits) {
	assert digits.length == 10;
	this.sourceDigits = digits;
    }

    /**
         * For use by the CharacterMultiMappedCardinalParser, which does not
         * make use of a single sourceDigits array.
         */
    protected CharacterMappedCardinalParser() {
	sourceDigits = null;
    }

    @Override
    public long getCharacterValue(char c) {
	System.out.println(1);
	if (c >= sourceDigits[0] && c <= sourceDigits[9]) {
	    return c - sourceDigits[0];
	} else {
	    throw new NumberFormatException("Character is not a digit in this language: " + c);
	}
    }

    @Override
    public WholeNumber getNumberFromFind(NumberMatch find) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public WholeNumber getNumberFromString(String strNumber) throws NumberFormatException {
	assert strNumber != null;

	char[] buf = new char[strNumber.length()];
	for (int i = 0; i < strNumber.length(); i++) {
	    buf[i] = (char) (getCharacterValue(strNumber.charAt(i)) + '0');
	}

	// TODO: Optimize this code a bit
	final String strResult = new String(buf);
	final long value = Long.parseLong(strResult);

	final int nLeadingZeros = countLeadingZeros(strNumber);
	return new WholeNumber(value, nLeadingZeros, AbstractNumber.Context.CARDINAL);
    }

}


