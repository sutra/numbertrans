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

import info.jonclark.util.ArrayUtils;
import info.jonclark.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;

/**
 * A grossly inefficient implementation of a suffix array.
 */
public class NaiveSuffixArray {

    private class Suffix implements Comparable<Suffix> {
	private final TokenArray tokArr;
	private final int pos;

	/**
         * @param arr
         * @param pos
         */
	public Suffix(TokenArray arr, int pos) {
	    this.tokArr = arr;
	    this.pos = pos;
	}

	public Suffix(String[] arr, int pos) {
	    this.tokArr = null;
	    this.pos = pos;
	}

	public int compareTo(Suffix suffixOther) {
	    for (int i = 0; i < this.tokArr.length() && i < suffixOther.tokArr.length(); i++) {
		String strMine = tokArr.get(i);
		String strOther = suffixOther.tokArr.get(i);
		int nResult = strMine.compareTo(strOther);
		if (nResult != 0) {
		    return nResult;
		}
	    }

	    if (this.tokArr.length() < suffixOther.tokArr.length())
		return 1;
	    else
		return 0;
	}

	public boolean startsWith(TokenArray suffixOther) {
	    return this.tokArr.startsWith(suffixOther);
	}

	public String toString() {
	    return pos + ":" + tokArr.toString();
	}
    }

    private final Suffix[] suffixArray;
    private final int nTokens;

    /**
         * Create a SuffixArray from a TokenArray. The use of a token array
         * ensures that all tokens are immutable.
         * 
         * @param tokens
         * @param nTotalTokens The count of total tokens in sentences. This MUST be accurate
         * @throws IOException
         */
    public NaiveSuffixArray(final Iterable<TokenArray> sentences, int nTotalTokens) {
	this.nTokens = nTotalTokens;

	suffixArray = new Suffix[nTokens];

	int nCurrentToken = 0;
	for (final TokenArray sentence : sentences) {
	    sentence.internTokens();
	    for (int i = 0; i < sentence.length(); i++) {
		suffixArray[nCurrentToken] = new Suffix(sentence.subarray(i), i);
		nCurrentToken++;
	    }
	}
	Arrays.sort(suffixArray);
    }
    
    public NaiveSuffixArray(final Iterable<TokenArray> sentences) {
	this(sentences, countTokens(sentences));
    }

    public NaiveSuffixArray(final TokenArray sentence) {
	this.nTokens = sentence.length();
	suffixArray = new Suffix[nTokens];
	sentence.internTokens();
	for (int i = 0; i < sentence.length(); i++)
	    suffixArray[i] = new Suffix(sentence.subarray(i), i);
	Arrays.sort(suffixArray);
    }
    
    private static int countTokens(Iterable<TokenArray> sentences) {
	int nTokenCount = 0;
	for (final TokenArray sentence : sentences)
	    nTokenCount += sentence.length();
	return nTokenCount;
    }

    public boolean containsSuffix(TokenArray suffix) {
	int nIndex = Arrays.binarySearch(suffixArray, new Suffix(suffix, -1));
	return nIndex >= 0;
    }

    /**
         * Returns the index (in the TokenArray passed to the constructor of
         * this SuffixArray) of the first occurance of the given suffix.
         * 
         * @param suffix
         * @return
         */
    public int indexOfSuffix(TokenArray suffix) {
	int nIndex = ArrayUtils.binarySearchForFirstIndex(suffixArray, new Suffix(suffix, -1));
	if (nIndex >= 0) {
	    return suffixArray[nIndex].pos;
	} else {
	    return -1;
	}
    }

    /**
         * Count the number of occurances of a given phrase.
         * <p>
         * Note, this method will intern() the contents of phrase, if such has
         * not already been done.
         * 
         * @param phrase
         * @return
         */
    public int countOccurances(TokenArray phrase) {
	phrase.internTokens();
	final Suffix desiredPhrase = new Suffix(phrase, -1);

	int nIndex = ArrayUtils.binarySearchForFirstIndex(suffixArray, desiredPhrase);
	if (nIndex >= 0) {
	    int count = 0;

	    while (nIndex < suffixArray.length && suffixArray[nIndex].startsWith(phrase)) {
		count++;
		nIndex++;
	    }

	    return count;
	} else {
	    return 0;
	}
    }

    /**
         * Get the count of tokens that were used to build this suffix array.
         * 
         * @return
         */
    public int size() {
	return nTokens;
    }

    public static void main(String... args) {
	TokenArray ta1 = new TokenArray(
		StringUtils.tokenize("a Jack jumped over a candle stick. a a"));
	TokenArray ta2 = new TokenArray(StringUtils.tokenize("sover"));

	NaiveSuffixArray sa = new NaiveSuffixArray(ta1);
	int nIndex = sa.indexOfSuffix(ta2);
	System.out.println(nIndex);
	System.out.println();

	TokenArray ta3 = new TokenArray(StringUtils.tokenize("a"));
	int count = sa.countOccurances(ta3);
	System.out.println(count);
    }
}
