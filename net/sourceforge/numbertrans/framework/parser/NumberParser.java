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

import net.sourceforge.numbertrans.framework.base.GeneralNumber;
import net.sourceforge.numbertrans.framework.base.NumberMatch;

/**
 * Inspect an isolated number in some source language
 */
public abstract class NumberParser {

    /**
         * Get a <code>Number</code> from a string known to be a number. If
         * the string is not a number, this method will throw a
         * <code>NumberFormatException</code>.
         * 
         * @param strNumber
         * @return
         * @throws NumberFormatException
         */
    public abstract GeneralNumber getNumberFromString(String strNumber) throws NumberFormatException;

    /**
         * Get a <code>Number</code> from a <code>NumberMatch</code>.
         * 
         * @param find
         * @return
         */
    public abstract GeneralNumber getNumberFromFind(NumberMatch find);

    /**
         * Get the value of a character known to be a digit. If the character is
         * not a digit, this method will throw s NumberFormatException.
         * 
         * @param c
         * @return
         * @throws NumberFormatException
         */
    public abstract long getCharacterValue(char c) throws NumberFormatException;

    /**
         * Count the number of digits having value zero at the beginning of the
         * given string.
         * 
         * @param str
         * @return
         */
    public int countLeadingZeros(final String str) {
	int count = 0;
	for (int i = 0; i < str.length() && getCharacterValue(str.charAt(i)) == 0; i++)
	    count++;
	return count;
    }
}
