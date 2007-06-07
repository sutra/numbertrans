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

import info.jonclark.util.FileUtils;
import info.jonclark.util.FormatUtils;
import info.jonclark.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Keeps a running total of unique (and non-unique) words encountered and
 * provides statistics accordingly.
 */
public class UniqueWordCounter {
    private static final long serialVersionUID = 5087475836426817361L;

    private HashMap<String, Integer> counts = new HashMap<String, Integer>();

    // String.CASE_INSENSITIVE_ORDER);
    private long uniqueCount = -1;
    private long nonUniqueCount = 0;
    private boolean alreadyInterned;
    private boolean useIntern;
    private final boolean caseSensitive;
    private boolean frozen;

    public UniqueWordCounter(boolean caseSensitive) {
	this.useIntern = false;
	this.alreadyInterned = false;
	frozen = false;
	this.caseSensitive = caseSensitive;
    }

    /**
         * Creates a new <code>UniqueWordCounter</code>
         * 
         * @param inputAlreadyInterned
         *                Are all strings to be passed to <code>addWord()</code>
         *                guaranteed to be <code>intern</code>ed? If not,
         *                they will be interned as they are added.
         */
    @Deprecated
    public UniqueWordCounter(boolean useIntern, boolean inputAlreadyInterned) {
	this.useIntern = useIntern;
	this.alreadyInterned = inputAlreadyInterned;
	frozen = false;
	this.caseSensitive = false;
    }

    /**
         * Creates a new <code>UniqueWordCounter</code> in a frozen state (new
         * words cannot be added) with the specified counts.
         * 
         * @param nonUniqueCount
         * @param uniqueCount
         */
    @Deprecated
    public UniqueWordCounter(long nonUniqueCount, long uniqueCount) {
	this.nonUniqueCount = nonUniqueCount;
	this.uniqueCount = uniqueCount;
	alreadyInterned = true;
	useIntern = false;
	frozen = true;

	// XXX: We actually don't know if it's case sensitive at this point
	caseSensitive = false;
    }

    /**
         * Put this object in a "frozen" state in which it will not accept new
         * words, but memory usage will be reduced.
         */
    @Deprecated
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

	if (!caseSensitive)
	    word = word.toLowerCase();

	// pool strings to minimize memory usage when using multiple word
	// counters
	// if (useIntern && !alreadyInterned)
	// word = word.intern();

	nonUniqueCount++;

	Integer count = counts.get(word);
	if (count == null)
	    counts.put(word, 1);
	else
	    counts.put(word, count + 1);
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
	    // if (useIntern && !alreadyInterned)
	    // word = word.intern();

	    word = word.toLowerCase();

	    Integer count = counts.get(word);
	    if (count == null)
		counts.put(word, 1);
	    else
		counts.put(word, count + 1);
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
	    throw new RuntimeException("Cannot add a frozen counter to an active counter.");
	} else {

	    // see if we can save time by just cloning the other counts
	    if (this.counts.size() == 0) {
		this.counts = cloneCounts(other.counts);
	    } else {
		for (final Entry<String, Integer> entry : other.counts.entrySet()) {
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

    public void serialize(File file) throws FileNotFoundException {
	PrintWriter out = new PrintWriter(file);
	out.println("frozen = " + frozen);
	out.println("useIntern = " + useIntern);
	out.println("alreadyInterned = " + alreadyInterned);
	out.println("caseSensitive = " + caseSensitive);
	out.println("non-unique count = " + this.getNonuniqueWordCount());
	out.println("unique count = " + this.getUniqueWordCount());
	if (!frozen) {
	    for (Entry<String, Integer> entry : counts.entrySet())
		out.println(entry.getKey() + "\t" + entry.getValue());
	}
	out.close();
    }

    public static UniqueWordCounter deserialize(File file) throws NumberFormatException,
	    IOException {
	BufferedReader in = new BufferedReader(new FileReader(file));
	boolean frozen = Boolean.parseBoolean(StringUtils.substringAfter(in.readLine(), " = "));
	boolean useIntern = Boolean.parseBoolean(StringUtils.substringAfter(in.readLine(), " = "));
	boolean alreadyInterned = Boolean.parseBoolean(StringUtils.substringAfter(in.readLine(),
		" = "));
	boolean caseSensitive = Boolean.parseBoolean(StringUtils.substringAfter(in.readLine(),
		" = "));
	int nonUniqueCount = Integer.parseInt(StringUtils.substringAfter(in.readLine(), " = "));
	int uniqueCount = Integer.parseInt(StringUtils.substringAfter(in.readLine(), " = "));

	UniqueWordCounter counter;
	if (frozen) {
	    counter = new UniqueWordCounter(nonUniqueCount, uniqueCount);
	} else {
	    HashMap<String, Integer> counts = new HashMap<String, Integer>(uniqueCount);

	    for (int i = 0; i < uniqueCount; i++) {
		String[] tokens = StringUtils.tokenize(in.readLine(), "\t");
		assert tokens.length == 2;
		counts.put(tokens[0], Integer.parseInt(tokens[1]));
	    }

	    counter = new UniqueWordCounter(caseSensitive);
	    counter.alreadyInterned = alreadyInterned;
	    counter.useIntern = useIntern;
	    counter.counts = counts;
	}

	return counter;
    }

    @SuppressWarnings("unchecked")
    private HashMap<String, Integer> cloneCounts(final HashMap<String, Integer> map) {
	return (HashMap<String, Integer>) map.clone();
    }

    public static void main(String[] args) throws Exception {
	if (args.length < 1) {
	    System.err.println("Usage: program <input_file_wildcard> (<tokens_only>)");
	    System.exit(1);
	}

	System.out.println("Finding files...");
	File[] files = FileUtils.getFilesFromWildcard(args[0]);
	System.out.println("Found " + files.length + " files to count.");

	UniqueWordCounter counter = new UniqueWordCounter(false, false);
	int nSentences = 0;
	long nWords = 0;

	for (final File file : files) {
	    BufferedReader in = new BufferedReader(new FileReader(file));
	    String line;
	    while ((line = in.readLine()) != null) {
		final List<String> words = CorpusUtils.tokenize(line);
		nSentences += CorpusUtils.countSentenceBoundaries(words);

		CorpusUtils.filterNonWords(words);
		if (args.length == 1)
		    counter.addWords(words);
		else
		    nWords += words.size();
	    }
	    in.close();
	}

	if (nWords == 0)
	    nWords = counter.getNonuniqueWordCount();

	final float mls = nSentences > 0 ? nWords / nSentences : 0;
	System.out.println("For file:\t" + args[0]);
	System.out.println("Total words:\t" + FormatUtils.formatLong(nWords));
	System.out.println("Unique words:\t" + FormatUtils.formatLong(counter.getUniqueWordCount()));
	System.out.println("Sentence count:\t" + FormatUtils.formatLong(nSentences));
	System.out.println("Mean Length of Sentence:\t" + FormatUtils.formatDouble2(mls));
    }
}
