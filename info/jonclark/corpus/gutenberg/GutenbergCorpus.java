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

import java.util.LinkedList;
import java.util.logging.Logger;

import info.jonclark.corpus.interfaces.CorpusStatistics;
import info.jonclark.log.LogUtils;

/**
 * @author Jonathan
 */
public class GutenbergCorpus implements CorpusStatistics {
    
    private final Logger log = LogUtils.getLogger();
    
    // do word counts for authors and stories on request
    // navigate website and cache downloaded documents
    
    // keep word and sentence statistics for entire corpus
    // use booleans to determine if counting will be done while loading/caching (default yes)
    
    // download catalog feed and then create index, based on author, this needs to be somewhat memory efficient
    private final LinkedList<CorpusAuthor> authors = new LinkedList<CorpusAuthor>();

    /**
     * Returns the GutenbergCorpus's logger. Use setParent on
     * your own subsystem's log to include these log messages.
     * 
     * @return
     */
    public final Logger getLog() {
        return log;
    }
    
    /* (non-Javadoc)
     * @see info.jonclark.corpus.interfaces.CorpusStatistics#getWordcount()
     */
    public long getWordcount() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see info.jonclark.corpus.interfaces.CorpusStatistics#getMeanLengthOfSentence()
     */
    public float getMeanLengthOfSentence() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see info.jonclark.corpus.interfaces.CorpusStatistics#getUniqueWordcount()
     */
    public long getUniqueWordcount() {
        // TODO Auto-generated method stub
        return 0;
    }

    public long getSentenceCount() {
	// TODO Auto-generated method stub
	return 0;
    }
}
