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
package info.jonclark.corpus;

import java.util.*;
import java.util.Map.Entry;

/**
 * Keeps a running total of unique (and non-unique) words encountered and
 * provides statistics accordingly.
 */
public class UniqueWordCounter {
    private TreeMap<String, Long> counts = new TreeMap<String, Long>();
    private long nonUniqueCount = 0;
    private final boolean alreadyInterned;

    /**
         * Creates a new <code>UniqueWordCounter</code>
         * 
         * @param inputAlreadyInterned
         *                Are all strings to be passed to <code>addWord()</code>
         *                guaranteed to be <code>intern</code>ed? If not,
         *                they will be interned as they are added.
         */
    public UniqueWordCounter(final boolean inputAlreadyInterned) {
	this.alreadyInterned = inputAlreadyInterned;
    }

    /**
         * Get the total number of non-unique words (that is, all tokens)
         * encountered so far
         * 
         * @return
         */
    public long getNonuniqueWordCount() {
	return nonUniqueCount;
    }

    /**
         * Get the total number of unique words encountered so far
         * 
         * @return
         */
    public long getUniqueWordCount() {
	return counts.size();
    }

    /**
         * Get an iterable set with all words and their assocatiated frequencies
         * 
         * @return
         */
    public Set<Entry<String, Long>> getEntrySet() {
	return counts.entrySet();
    }

    /**
         * Adds the word with a frequency of 1 if not previously encountered.
         * Otherwise, increments the frequency for this word. Calls
         * <code>intern()</code> on the word to minimize memory usage.
         * 
         * @param word
         *                A word as a single token
         */
    public void addWord(String word) {
	if (!alreadyInterned)
	    word = word.intern(); // pool strings to minimize memory usage

	nonUniqueCount++;

	Long count = counts.get(word);
	if (count == null)
	    counts.put(word, 1l);
	else
	    counts.put(word, count++);
    }
}
