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

import java.io.IOException;

/**
 * An implementation of a Suffix Array as described in "Suffix arrays: A new
 * method for on-line string searches" by Udi Manber and Gene Myers.
 */
public class SuffixArray {

    private final TokenArray suffixArray;
    private final int[] pos;
    private final int[] prm;
    private final boolean[] bh;

    /**
         * Create a SuffixArray from a TokenArray. The use of a token array
         * ensures that all tokens are immutable.
         * 
         * @param tokens
         * @throws IOException
         */
    public SuffixArray(final TokenArray tokens, final String alphabet) throws IOException {
	suffixArray = tokens;

	final int n = tokens.length();
	pos = new int[n - 1];
	prm = new int[n - 1];
	bh = new boolean[n];

	buildSuffixArray(tokens, alphabet);
    }

    private void buildSuffixArray(final TokenArray tokens, final String alphabet)
	    throws IOException {
	
	// temporary variables, used only for creation
	final int n = tokens.length();
	final int[] count = new int[n - 1];
	final boolean[] b2h = new boolean[n];

	// Sort implemented with 3N positive integers and 2N booleans.
	// TODO: Count can be eliminated and booleans folded into sign bits, so
	// product a 2N integer sort.

	// First phase buckt sort, Bucket and Link overlay pos and prm,
	// respectively

	// define "overlay" variables from the paper
	// TODO: Refactor these away later
	final int[] bucket = pos;
	final int[] link = prm;

	for (int i = 0; i < alphabet.length(); i++) {
	    // XXX: Bucket AT a's position?
	    bucket[i] = -1;
	}

	for (int i = 0; i <= n - 1; i++) {
	    // XXX: a[i] meaning sigma[i]?
	    int temp = bucket[alphabet.charAt(i)];
//	    bucket[a[i]] = i;
	    link[i] = temp;
	}

	int c = 0;

    }
}
