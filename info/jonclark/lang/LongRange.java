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
package info.jonclark.lang;

import info.jonclark.util.StringUtils;

/**
 * This class holds 2 longs. Simple, right?
 * <p>
 * <b>NOTE:</b> This class does not implement the <code>Comparable</code>
 * interface because correct ordering cannot be guaranteed for objects that are
 * farther than <code>Integer.MAX_VALUE</code> apart.
 */
public class LongRange {
    public long first;
    public long last;

    public LongRange(final long first, final long last) {
	assert first <= last;
	this.first = first;
	this.last = last;
    }

    public long length() {
	return last - first;
    }

    public boolean isInRange(long n) {
	return n >= first && n <= last;
    }

    public boolean equals(Object o) {
	if (o instanceof LongRange) {
	    final LongRange other = (LongRange) o;
	    return other.first == this.first && other.last == this.last;
	} else {
	    return false;
	}
    }

    /**
     * Parses a long range of either the form "x-y" or "x"
     * 
     * @param str
     * @return
     */
    public static LongRange parseLongRange(String str) {
	if (str.contains("-")) {
	    String[] tokens = StringUtils.split(str, "-", 2);
	    long first = Long.parseLong(tokens[0].trim());
	    long second = Long.parseLong(tokens[1].trim());
	    return new LongRange(first, second);
	} else {
	    long x = Long.parseLong(str.trim());
	    return new LongRange(x, x);
	}
    }

    public String toString() {
	return first + "-" + last;
    }
}
