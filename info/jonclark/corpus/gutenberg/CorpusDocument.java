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

import java.io.*;

import info.jonclark.corpus.UniqueWordCounter;
import info.jonclark.corpus.interfaces.*;

/**
 * @author Jonathan
 */
public abstract class CorpusDocument implements CorpusStatistics, Comparable {

    private static final boolean useIntern = false;
    private String author = "";
    private String title = "";
    private String id = "NONE";
    private String language = "";
    protected UniqueWordCounter wordCount = new UniqueWordCounter(useIntern, false);
    protected long nSentenceCount = 0;

    public UniqueWordCounter getUniqueWordCounter() {
	return wordCount;
    }

    public long getSentenceCount() {
	return nSentenceCount;
    }

    /**
         * @return Returns the author.
         */
    public String getAuthor() {
	return author;
    }

    /**
         * @param author
         *                The author to set.
         */
    public void setAuthor(String author) {
	this.author = author;
    }

    /**
         * @return Returns the id.
         */
    public String getId() {
	return id;
    }

    /**
         * @param id
         *                The id to set.
         */
    public void setId(String id) {
	this.id = id;
    }

    /**
         * @return Returns the title.
         */
    public String getTitle() {
	return title;
    }

    /**
         * @param title
         *                The title to set.
         */
    public void setTitle(String title) {
	this.title = title;
    }

    /**
         * Parse a document for this corpus type
         * 
         * @param in
         * @throws IOException
         */
    public abstract void parse(File file) throws IOException;

    // THIS CLASS IS ABSTRACT BECAUSE EACH TYPE OF
    // CORPUS DOCUMENT NEEDS TO KNOW HOW TO PARSE
    // ITS OWN TYPE OF DOCUMNET
    // THAT METHOD WILL BE STATIC AND RETURN A NEW
    // CORPUS DOCUMENT
    // THIS WILL NOT BE SERIALIZABLE... or will it?

    // come up with heuristic to take out garbage
    // at beginning and end of files

    /**
         * what language was this document written in?
         * 
         * @return Returns the language.
         */
    public String getLanguage() {
	return language;
    }

    /**
         * @param language
         *                The language to set.
         */
    public void setLanguage(String language) {
	this.language = language;
    }

    /**
         * Determine if 2 documents' ID's are the same
         * 
         * @return True if ID's match
         */
    public boolean equals(Object obj) {
	if (obj instanceof CorpusDocument) {
	    return ((CorpusDocument) obj).id.equals(this.id);
	} else {
	    return false;
	}
    }

    /*
         * (non-Javadoc)
         * 
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
    public int compareTo(Object obj) {
	if (obj instanceof CorpusDocument) {
	    return ((CorpusDocument) obj).id.compareTo(this.id);
	} else {
	    return -1;
	}
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

    public float getMeanLengthOfSentence() {
	if (nSentenceCount != 0)
	    return (float) getWordcount() / (float) nSentenceCount;
	else
	    return 0;
    }

    public void setCounts(long nonUniqueWordCount, long uniqueWordCount, long sentenceCount) {
	nSentenceCount = sentenceCount;
	if (uniqueWordCount == 0 && nonUniqueWordCount == 0)
	    wordCount = new UniqueWordCounter(useIntern, false);
	else
	    wordCount = new UniqueWordCounter(nonUniqueWordCount, uniqueWordCount);
    }
}
