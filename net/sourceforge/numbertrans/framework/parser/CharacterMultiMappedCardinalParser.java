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

import java.util.Vector;

import net.sourceforge.numbertrans.framework.base.WholeNumber;

/**
 * A class for converting out of number systems that have a Many-to-1 character
 * correspondence with the arabic numeral system. e.g. X and Y are both source
 * digits that should be translated into the target digit Z.
 */
public class CharacterMultiMappedCardinalParser extends CharacterMappedCardinalParser {

    private final Vector<char[]> sourceDigitSets;

    /**
         * Create a new <code>CharacterMappedCardinalParser</code> object.
         * 
         * @param digits
         *                An array of length 10 containing the digits in the
         *                source language with the character for 0 as the zeroth
         *                element.
         */
    public CharacterMultiMappedCardinalParser(Vector<char[]> digitSets) {
	for (char[] digitSet : digitSets)
	    assert digitSet.length == 10;
	this.sourceDigitSets = digitSets;
    }

    @Override
    public long getCharacterValue(char c) {
	for (char[] sourceDigits : sourceDigitSets) {
	    if (c >= sourceDigits[0] && c <= sourceDigits[9]) {
		return c - sourceDigits[0];
	    }
	}
	throw new NumberFormatException("Character is not a digit in this language: " + c);
    }
    
    /**
     * Test method
     */
    public static void main(String[] args) {
	Vector<char[]> v = new Vector<char[]>();
	v.add(new char[] {'0','1','2','3','4','5','6','7','8','9'});
	CharacterMultiMappedCardinalParser x = new CharacterMultiMappedCardinalParser(v);
	x.getNumberFromString("10");
    }
}
