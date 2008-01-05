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
package net.sourceforge.numbertrans.languages.roman;

import net.sourceforge.numbertrans.framework.base.AbstractNumber;
import net.sourceforge.numbertrans.framework.base.NumberMatch;
import net.sourceforge.numbertrans.framework.base.WholeNumber;
import net.sourceforge.numbertrans.framework.base.AbstractNumber.Context;
import net.sourceforge.numbertrans.framework.parser.NumberParser;

/**
 * Parses Roman numerals using the following values:<br>
 * M 1000<br>
 * D 500<br>
 * C 100<br>
 * L 50<br>
 * X 10<br>
 * V 5<br>
 * I 1<br>
 */
public class RomanCardinalParser extends NumberParser {

    @Override
    public long getCharacterValue(char c) throws NumberFormatException {
	switch (Character.toUpperCase(c)) {
	case 'M':
	    return 1000;
	case 'D':
	    return 500;
	case 'C':
	    return 100;
	case 'L':
	    return 50;
	case 'X':
	    return 10;
	case 'V':
	    return 5;
	case 'I':
	    return 1;
	}
	throw new NumberFormatException("Unknown character: " + c);
    }

    @Override
    public AbstractNumber getNumberFromFind(NumberMatch find) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public AbstractNumber getNumberFromString(String strNumber) throws NumberFormatException {
	int totalValue = 0;
	int localValue = 0;
	int prevDigit = 0;

	for (int i = 0; i < strNumber.length(); i++) {
	    int currentDigit = (int) getCharacterValue(strNumber.charAt(i));

	    if (currentDigit < prevDigit) {
		totalValue += localValue;
		localValue = currentDigit;
	    } else if(currentDigit > prevDigit){
		localValue = currentDigit - localValue;
	    } else {
		localValue += currentDigit;
	    }
	    prevDigit = currentDigit;
	}
	
	totalValue += localValue;

	return new WholeNumber(totalValue, 0, Context.CARDINAL);
    }

}
