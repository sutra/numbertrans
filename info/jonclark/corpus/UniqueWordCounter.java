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

import info.jonclark.util.FormatUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

/**
 * Keeps a running total of unique (and non-unique) words encountered and
 * provides statistics accordingly.
 */
public class UniqueWordCounter implements Serializable {
    private static final long serialVersionUID = 5087475836426817361L;
    private HashMap<String, Integer> counts = new HashMap<String, Integer>();
    // String.CASE_INSENSITIVE_ORDER);
    private long uniqueCount = -1;
    private long nonUniqueCount = 0;
    private final boolean alreadyInterned;
    private final boolean useIntern;
    private boolean frozen;

    /**
         * Creates a new <code>UniqueWordCounter</code>
         * 
         * @param inputAlreadyInterned
         *                Are all strings to be passed to <code>addWord()</code>
         *                guaranteed to be <code>intern</code>ed? If not,
         *                they will be interned as they are added.
         */
    public UniqueWordCounter(boolean useIntern, boolean inputAlreadyInterned) {
	this.useIntern = useIntern;
	this.alreadyInterned = inputAlreadyInterned;
	frozen = false;
    }

    /**
         * Creates a new <code>UniqueWordCounter</code> in a frozen state (new
         * words cannot be added) with the specified counts.
         * 
         * @param nonUniqueCount
         * @param uniqueCount
         */
    public UniqueWordCounter(long nonUniqueCount, long uniqueCount) {
	this.nonUniqueCount = nonUniqueCount;
	this.uniqueCount = uniqueCount;
	alreadyInterned = true;
	useIntern = false;
	frozen = true;
    }

    /**
         * Put this object in a "frozen" state in which it will not accept new
         * words, but memory usage will be reduced.
         */
    public void freezeCounts() {
	frozen = true;
	uniqueCount = counts.size();
	counts = null;
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
	if (frozen)
	    return uniqueCount;
	else
	    return counts.size();
    }

    /**
         * Get an iterable set with all words and their assocatiated frequencies
         * 
         * @return
         */
    public Set<Entry<String, Integer>> getEntrySet() {
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
	if (frozen)
	    throw new RuntimeException("Cannot add new words when UniqueWordCounter is frozen");

	// pool strings to minimize memory usage when using multiple word
	// counters
	if (useIntern && !alreadyInterned)
	    word = word.intern();

	nonUniqueCount++;

	Integer count = counts.get(word);
	if (count == null)
	    counts.put(word, 1);
	else
	    counts.put(word, count++);
    }

    /**
         * Adds each word in <code>words</code>. Uses a frequency of 1 if a
         * word previously encountered. Otherwise, increments the frequency for
         * that word. Calls <code>intern()</code> on the word to minimize
         * memory usage.
         * 
         * @param word
         *                A word as a single token
         */
    public void addWords(List<String> words) {
	if (frozen)
	    throw new RuntimeException("Cannot add new words when UniqueWordCounter is frozen");

	nonUniqueCount += words.size();

	for (String word : words) {
	    // pool strings to minimize memory usage when using multiple
	    // word
	    // counters
	    if (useIntern && !alreadyInterned)
		word = word.intern();

	    Integer count = counts.get(word);
	    if (count == null)
		counts.put(word, 1);
	    else
		counts.put(word, count++);
	}
    }

    /**
         * Adds the unique and non-unique values of another counter to this one
         * without loss of data.
         * 
         * @param other
         *                Another <code>UniqueWordCounter</code> whose value
         *                will be added to this one.
         */
    public void addCounter(UniqueWordCounter other) {
	this.nonUniqueCount += other.nonUniqueCount;

	if (other.frozen) {
	    // TODO: FIXME
	} else {

	    // see if we can save time by just cloning the other counts
	    if (this.counts.size() == 0) {
		this.counts = cloneCounts(other.counts);
	    } else {
		for (final Map.Entry<String, Integer> entry : other.counts.entrySet()) {
		    final String word = entry.getKey();
		    final Integer otherCount = entry.getValue();

		    Integer prevCount = counts.get(word);
		    if (prevCount == null)
			counts.put(word, otherCount);
		    else
			counts.put(word, prevCount + otherCount);
		}
	    } // end if this.count.size() == 0
	} // end if other.frozen
    }

    public int getCountForWord(final String word) {
	Integer count = counts.get(word);
	if (count == null) {
	    return 0;
	} else {
	    return count.intValue();
	}
    }

    @SuppressWarnings("unchecked")
    private HashMap<String, Integer> cloneCounts(final HashMap<String, Integer> map) {
	return (HashMap<String, Integer>) map.clone();
    }

    public static void main(String[] args) throws Exception {
	if (args.length != 1) {
	    System.err.println("Usage: program <input_file>");
	    System.exit(1);
	}

	UniqueWordCounter counter = new UniqueWordCounter(false, false);
	int nSentences = 0;
	BufferedReader in = new BufferedReader(new FileReader(args[0]));
	String line;
	while ((line = in.readLine()) != null) {
	    final List<String> words = CorpusUtils.tokenize(line);
	    nSentences += CorpusUtils.countSentenceBoundaries(words);

	    CorpusUtils.filterNonWords(words);
	    counter.addWords(words);
	}
	in.close();

	final float mls = nSentences > 0 ? counter.getNonuniqueWordCount() / nSentences : 0;
	System.out.println("For file:\t" + args[0]);
	System.out.println("Total words:\t"
		+ FormatUtils.formatLong(counter.getNonuniqueWordCount()));
	System.out.println("Unique words:\t" + FormatUtils.formatLong(counter.getUniqueWordCount()));
	System.out.println("Sentence count:\t" + FormatUtils.formatLong(nSentences));
	System.out.println("Mean Length of Sentence:\t" + FormatUtils.formatDouble2(mls));
    }
}
