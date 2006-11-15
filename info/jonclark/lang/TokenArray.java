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

/**
 * A String-style immutable array of tokens
 */
public final class TokenArray {
    private final String[] tokens;
    private final int offset;
    private final int count;

    public TokenArray(final String[] tokens) {
	this.offset = 0;
	this.count = tokens.length;

	this.tokens = new String[tokens.length];
	for (int i = 0; i < tokens.length; i++)
	    this.tokens[i] = tokens[i];
    }

    public TokenArray(final String[] tokens, final IntRange range) {
	this.offset = 0;
	this.count = range.length();

	this.tokens = new String[range.length()];
	for (int i = range.first; i < range.last; i++)
	    this.tokens[i - range.first] = tokens[i];
    }

    public TokenArray(final String[] tokens, final int nFirst, final int nLast) {
	assert nFirst <= nLast : "nFirst > nLast";

	this.offset = 0;
	this.count = nLast-nFirst;

	this.tokens = new String[nLast - nFirst];
	for (int i = nFirst; i < nLast; i++)
	    this.tokens[i - nFirst] = tokens[i];
    }

    public TokenArray(final TokenArray arr, final int nFirst, final int nLast) {
	assert nFirst <= nLast : "nFirst > nLast";
	
	this.offset = nFirst;
	this.count = nLast - nFirst;

	this.tokens = arr.tokens;
    }
    
    public TokenArray(final TokenArray arr, final IntRange range) {	
	this.offset = range.first;
	this.count = range.length();

	this.tokens = arr.tokens;
    }

    public String tokenAt(int i) {
	if(i < offset || i >= offset+count)
	    throw new ArrayIndexOutOfBoundsException("Token index out of bounds: " + i);
	
	return tokens[i+offset];
    }
    
    public int length() {
	return count;
    }
}
