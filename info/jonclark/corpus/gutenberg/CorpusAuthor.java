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
package info.jonclark.corpus.gutenberg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

import info.jonclark.corpus.UniqueWordCounter;
import info.jonclark.corpus.interfaces.CorpusStatistics;

/**
 * @author Jonathan
 */
public class CorpusAuthor implements CorpusStatistics, Serializable, Comparator<CorpusAuthor> {

    private static final boolean useIntern = false;
    private static final long serialVersionUID = -7109345932526990961L;
    private ArrayList<CorpusDocument> documents = new ArrayList<CorpusDocument>();
    private UniqueWordCounter wordCount;
    private long nSentenceCount = 0;
    private final String authorName;

    public CorpusAuthor(String authorName) {
	this.authorName = authorName;
    }

    public void addDocument(CorpusDocument doc) {
	documents.add(doc);
    }

    public void updateStatistics() {
	nSentenceCount = 0;

	// save time by only referencing the unique word count if
	// there's only one document for this word count
	if (documents.size() > 1) {
	    wordCount = new UniqueWordCounter(useIntern, false);
	}

	for (final CorpusDocument doc : documents) {
	    nSentenceCount += doc.getSentenceCount();

	    // save time by only referencing the unique word count if
	    // there's only one document for this word count
	    if (documents.size() == 1) {
		this.wordCount = doc.getUniqueWordCounter();
	    } else {
		wordCount.addCounter(doc.getUniqueWordCounter());
	    }
	}
    }

    public ArrayList<CorpusDocument> getDocuments() {
	return documents;
    }

    public String getAuthorName() {
	return authorName;
    }

    public float getMeanLengthOfSentence() {
	if (nSentenceCount > 0)
	    return wordCount.getNonuniqueWordCount() / nSentenceCount;
	else
	    return 0;
    }

    public long getSentenceCount() {
	return nSentenceCount;
    }

    public long getUniqueWordcount() {
	if (wordCount == null)
	    return 0;
	else
	    return wordCount.getUniqueWordCount();
    }

    public long getWordcount() {
	if (wordCount == null)
	    return 0;
	else
	    return wordCount.getNonuniqueWordCount();
    }
    
    public void setCounts(long nonUniqueWordCount, long uniqueWordCount, long sentenceCount) {
	nSentenceCount = sentenceCount;
	wordCount = new UniqueWordCounter(nonUniqueWordCount, uniqueWordCount);
    }

    public int compare(CorpusAuthor o1, CorpusAuthor o2) {
	return o1.authorName.compareToIgnoreCase(o2.authorName);
    }
}
