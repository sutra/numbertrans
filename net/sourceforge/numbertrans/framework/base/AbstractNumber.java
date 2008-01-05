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

/**
 * A highly generic representation of a number that allows for easy conversion
 * between number systems of various languages. This class must be extended by a
 * subclass that defines T. This allows for more accurate representations of
 * whole numbers vs. decimals vs. fractions.
 */
public abstract class AbstractNumber {

    /**
         * CONTEXT_CARDINAL includes items that are counted in Chinese (i.e. The
         * ZH/JP scribes should eventually produce the correct counters)
         */
    public enum Context {
	CARDINAL, ORDINAL, DECIMAL, FRACTION, DATE, TIME, PHONE, CURRENCY, UNKNOWN
    };

    protected final int nLeadingZeros;
    protected final Context context;

    protected AbstractNumber(final int nLeadingZeros, final Context context) {
	this.nLeadingZeros = nLeadingZeros;
	this.context = context;
    }

    /**
         * @return the number of leading zeros for this number
         */
    public int getLeadingZeros() {
	return nLeadingZeros;
    }

    protected boolean equalsSuper(Object o) {
	if (o instanceof AbstractNumber) {
	    final AbstractNumber other = (AbstractNumber) o;
	    return this.nLeadingZeros == other.nLeadingZeros && this.context == other.context;
	} else {
	    return false;
	}
    }
}
