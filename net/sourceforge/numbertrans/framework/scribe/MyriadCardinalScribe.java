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
package net.sourceforge.numbertrans.framework.scribe;

import info.jonclark.util.StringUtils;
import net.sourceforge.numbertrans.framework.base.MyriadNumeralSet;
import net.sourceforge.numbertrans.framework.base.WholeNumber;

public class MyriadCardinalScribe extends CardinalScribe {

    private MyriadNumeralSet numerals;

    public MyriadCardinalScribe(Form form, MyriadNumeralSet numerals) {
	super(form);
	this.numerals = numerals;
    }

    @Override
    public String getCardinalString(WholeNumber number) {
	// TODO: years such as 2004
	// TODO: leading zeros
	
	final long actualValue = number.getValue();
	final StringBuilder builder = new StringBuilder();
	
	long totalValue = actualValue;
	long localMultiplier = 1;
	long bigMultiplier = 1;
	while(totalValue > 0) {
	    
	    // TODO: properly handle "bigMultiplier" starting with "man" character
	    
	    // append the large number first, since we're generating in reverse
	    if(localMultiplier >= 10) {
		builder.append(numerals.getLargeNumber(localMultiplier));
	    }
	    
	    final int digit = (int) (totalValue % 10);
	    builder.append(numerals.getDigit(digit));
	    
	    // update values for next iteration
	    totalValue /= 10;	    
	    localMultiplier *= 10;
	    
	    if(localMultiplier >= 10000) {
		bigMultiplier *= 10000;
		localMultiplier = 1;
		builder.append(numerals.getLargeNumber(bigMultiplier));
	    }
	}
	
	// get the result and reverse it, since we started from the opposite end
	String result = builder.toString();
	result = StringUtils.reverse(result);

	return result;
    }

    @Override
    public Form[] getSupportedForms() {
	return new Form[] { Form.SHORT };
    }
}
