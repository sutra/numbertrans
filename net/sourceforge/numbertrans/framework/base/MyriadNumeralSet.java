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

public class MyriadNumeralSet {
    private final String[] digits; // digits 0-9
    private final String[] largeNumbers; // powers of 10

    public MyriadNumeralSet(String[] digits, String[] largeNumbers) {
	assert digits.length == 10 : "digits must have length 10 (contains 0-9)";

	this.digits = digits;
	this.largeNumbers = largeNumbers;
    }

    public String getDigit(int value) {
	assert value >= 0 && value <= 9 : "Digits have a value from 0 to 9; received value = "
		+ value;
	return digits[value];
    }

    /**
         * Note that this method is only defined for <code>magnitude</code>s
         * that are powers of 10.
         * 
         * @param magnitude
         * @return
         */
    public String getLargeNumber(long magnitude) {
	assert magnitude % 10 == 0 : "Only powers of ten are valid for this method.";
	int nPowerOfTen = (int) Math.log10(magnitude);
	assert nPowerOfTen > 0 : "The power of ten " + nPowerOfTen
		+ " is not valid (it is less than 1).";
	return largeNumbers[nPowerOfTen - 1];
    }
}
