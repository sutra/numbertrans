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

import java.util.Collection;
import java.util.Iterator;

import info.jonclark.util.StringUtils;

/**
 * A immutable array of String tokens. This allows for String-style
 * optimizations since the tokens can no longer be modified.
 */
// TODO: Create a TokenArrayBuilder that allows for efficient creation
public final class TokenArray implements Iterable<String> {
    private final String[] tokens;
    private final int offset;
    private final int count;
    private boolean isInterned = false;

    /**
         * This constructor gives the caller the option of whether or not to
         * create a copy of the array. Hence, it is protected since it has the
         * potential to violate the immutability of this class. Only classes
         * within the lang package should use this constructor.
         * <p>
         * Note: If the length of tokens is 0, then all other parameters are
         * ignored and an empty token array is returned.
         * 
         * @param tokens
         * @param createCopy
         */
    protected TokenArray(final String[] tokens, int nFirst, int nLast, boolean createCopy) {
	if (tokens.length == 0) {

	    this.offset = 0;
	    this.count = 0;
	    this.tokens = tokens;

	} else if (createCopy) {
	    // we're not special... go ahead and do the expensive copy

	    assert nFirst <= nLast : "nFirst > nLast";

	    this.offset = 0;
	    this.count = nLast - nFirst + 1;

	    this.tokens = new String[this.count];
	    for (int i = nFirst; i <= nLast; i++)
		this.tokens[i - nFirst] = tokens[i];
	} else {
	    // this is a special invocation callable only from within the
	    // info.jonclark.lang package.
	    assert nFirst <= nLast : "nFirst > nLast";

	    this.offset = nFirst;
	    this.count = nLast - nFirst + 1;

	    this.tokens = tokens;
	}

	assert offset + count <= tokens.length;
    }

    /**
         * Create a new token array from a String[] by copying all elements to
         * an internal buffer.
         * 
         * @param tokens
         */
    public TokenArray(final String[] tokens) {
	this(tokens, 0, tokens.length - 1, true);
    }

    public TokenArray(final String[] tokens, final IntRange range) {
	this(tokens, range.first, range.last, true);
    }

    public TokenArray(final String[] tokens, final int nFirst, final int nLast) {
	this(tokens, nFirst, nLast, true);
    }

    public TokenArray(Collection<String> tokens) {
	// we have to use a private dummy constructor so that we only calculate
        // size once
	this(tokens, tokens.size());
    }

    private TokenArray(Collection<String> tokens, int nSize) {
	this(tokens.toArray(new String[nSize]), 0, nSize - 1, false);
    }

    /**
         * Efficiently create a new TokenArray that is a subset of another
         * TokenArray.
         * 
         * @param arr
         * @param nFirst
         * @param nLast
         */
    public TokenArray(final TokenArray arr, final int nFirst, final int nLast) {
	this(arr.tokens, arr.offset + nFirst, arr.offset + nLast, false);
	this.isInterned = arr.isInterned;
	assertInternStatus();
    }

    public TokenArray(final TokenArray arr, final IntRange range) {
	this(arr.tokens, arr.offset + range.first, arr.offset + range.last, false);
	this.isInterned = arr.isInterned;
	assertInternStatus();
    }

    private void assertInternStatus() {
	if (isInterned) {
	    for (int i = 0; i < tokens.length; i++) {
		if (tokens[i] != null)
		    assert tokens[i] == tokens[i].intern() : "Invalid intern status";
	    }
	}
    }

    public TokenArray subarray(int nFirst) {
	return new TokenArray(this, nFirst, this.count - 1);
    }

    public TokenArray subarray(int nFirst, int nLast) {
	return new TokenArray(this, nFirst, nLast);
    }

    /**
         * Gets a subarray of this token end of length specified by
         * nCountFromEnd.
         * 
         * @param nTokensToCut
         * @return
         */
    public TokenArray cutTokensFromEnd(int nTokensToCut) {
	return new TokenArray(this, 0, this.length() - nTokensToCut - 1);
    }

    public String get(int i) {
	if (i < 0 || i + offset >= tokens.length)
	    throw new ArrayIndexOutOfBoundsException("Token index out of bounds: " + i);

	return tokens[i + offset];
    }

    public final String getLastToken() {
	return tokens[offset + count - 1];
    }

    public final int length() {
	return count;
    }

    public void internTokens() {
	if (!isInterned) {
	    for (int i = 0; i < tokens.length; i++) {
		if (tokens[i] != null)
		    tokens[i] = tokens[i].intern();
	    }
	    isInterned = true;
	}
    }

    public boolean startsWith(TokenArray prefix, int toffset) {
	String ta[] = tokens;
	int to = offset + toffset;
	String pa[] = prefix.tokens;
	int po = prefix.offset;
	int pc = prefix.count;
	// Note: toffset might be near -1>>>1.
	if ((toffset < 0) || (toffset > count - pc)) {
	    return false;
	}
	while (--pc >= 0) {
	    if (this.isInterned && prefix.isInterned) {
		assert (ta[to] == pa[po]) == (ta[to].equals(pa[po]));

		if (ta[to++] != pa[po++]) {
		    return false;
		}
	    } else {
		if (!ta[to++].equals(pa[po++])) {
		    return false;
		}
	    }
	}
	return true;
    }

    public boolean startsWith(TokenArray prefix) {
	return startsWith(prefix, 0);
    }

    public final String toString() {
	return StringUtils.untokenize(tokens, offset, offset + count - 1);
    }

    public boolean isInterned() {
	return isInterned;
    }

    public Iterator<String> iterator() {
	return new Iterator<String>() {
	    private int i = 0;

	    public boolean hasNext() {
		return i < tokens.length;
	    }

	    public String next() {
		return tokens[i++];
	    }

	    public void remove() {
		throw new UnsupportedOperationException(
			"Cannot remove an item from an immutable TokenArray");
	    }

	};
    }
}
