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

import java.util.List;

/**
 * A means of creating a <code>TokenArray</code> while saving as many copy
 * operations as possible. (Note that the array will grow as necessary.)
 * <p>
 * Recommended Usage: Make all changes necessary to the array, and THEN call
 * <code>toTokenArray()</code>. This will avoid the need for an expensive
 * array copy.
 */
public class TokenArrayBuilder {
    private String[] tokens;
    private boolean isExternalReferenceToTokens = false;
    private TokenArray tokenArray = null;
    private int nSize = 0;

    /**
         * Create a new <code>TokenArrayBuilder</code> with the specified
         * capacity.
         * 
         * @param initialSize
         */
    public TokenArrayBuilder(int initialSize) {
	tokens = new String[initialSize];
    }

    /**
         * Get the token at the specified array location.
         * 
         * @param i
         * @return
         */
    public String tokenAt(int i) {
	return tokens[i];
    }

    /**
         * Add a token as the last element of this TokenArray, growing the array
         * if necessary. If any <code>toTokenArray()</code> has been called
         * previously, those <code>TokenArray</code>s will not be affected by
         * this change.
         * 
         * @param value
         */
    public void addToken(String value) {
	if (isExternalReferenceToTokens)
	    duplicateTokens();

	ensureCapacity(nSize + 1);
	tokens[nSize] = value;
	nSize++;
    }

    /**
         * Add a list of tokens as the last elements of this TokenArray, growing
         * the array if necessary. If any <code>toTokenArray()</code> has been
         * called previously, those <code>TokenArray</code>s will not be
         * affected by this change.
         * 
         * @param value
         */
    public void addTokens(List<String> values) {
	if (isExternalReferenceToTokens)
	    duplicateTokens();

	ensureCapacity(nSize + values.size());
	int i = 0;
	for (final String value : values) {
	    tokens[nSize + i] = value;
	    i++;
	}

	nSize += values.size();
    }

    /**
         * Add a list of tokens as the last elements of this TokenArray, growing
         * the array if necessary. If any <code>toTokenArray()</code> has been
         * called previously, those <code>TokenArray</code>s will not be
         * affected by this change.
         * 
         * @param value
         */
    public void addTokens(String[] values) {
	if (isExternalReferenceToTokens)
	    duplicateTokens();

	ensureCapacity(nSize + values.length);
	for (int i = 0; i < values.length; i++) {
	    tokens[nSize + i] = values[i];
	}

	nSize += values.length;
    }

    /**
         * Set the token at the specified array location FOR THIS BUILDER. If
         * any <code>toTokenArray()</code> has been called previously, those
         * <code>TokenArray</code>s will not be affected by this change.
         * 
         * @param i
         * @param value
         */
    public void setTokenAt(int i, String value) {
	if (isExternalReferenceToTokens)
	    duplicateTokens();

	// nothing else is currently using tokens now
	tokens[i] = value;
    }

    private void duplicateTokens() {
	// create an array that no one else has access to
	String[] oldTokens = tokens;
	tokens = new String[oldTokens.length];
	System.arraycopy(oldTokens, 0, tokens, 0, nSize);

	// since we're baout to modify the array, make sure we return a fresh
	// version next time toTokenArray() is called
	tokenArray = null;

	// we have now freed all external references to tokens
	isExternalReferenceToTokens = false;
    }

    /**
         * Get an immutable <code>TokenArray</code> with the same contents as
         * this builder.
         * 
         * @return
         */
    public TokenArray toTokenArray() {
	if (tokenArray == null) {
	    // yes, this line is okay for zero-length arrays b/c TokenArray
	    // constrcutor
	    tokenArray = new TokenArray(tokens, 0, nSize - 1, false);
	}

	isExternalReferenceToTokens = true;
	return tokenArray;
    }

    /**
         * Returns the length of this TokenArrayBuilder, which was passed to the
         * constructor.
         * 
         * @return
         */
    public int length() {
	return tokens.length;
    }

    /**
         * Grows the array if necessary, in the same style as ArrayList
         * 
         * @param minCapacity
         */
    public void ensureCapacity(int minCapacity) {
	int oldCapacity = tokens.length;
	if (minCapacity > oldCapacity) {
	    String[] oldData = tokens;
	    int newCapacity = (oldCapacity * 3) / 2 + 1;
	    if (newCapacity < minCapacity)
		newCapacity = minCapacity;
	    tokens = new String[newCapacity];
	    System.arraycopy(oldData, 0, tokens, 0, nSize);
	}
    }
}
