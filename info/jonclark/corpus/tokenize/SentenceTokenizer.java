/*
 * Created on May 28, 2007
 */
package info.jonclark.corpus.tokenize;

import java.util.ArrayList;
import java.util.Properties;

public class SentenceTokenizer {
    
    public SentenceTokenizer(Properties props) {
	
    }
    
    private boolean isSentenceBoundary(final String str) {
	if(str.length() != 1) {
	    return false;
	} else {
	    return (EnglishTokenizer.SENTENCE_PUNCTUATION.indexOf(str.charAt(0)) != -1);
	}
    }
    
    /**
         * Returns sentences
         * 
         * @param text
         * @return
         */
    public String[][] tokenizeToSentences(final Iterable<String> wordTokens) {
	final ArrayList<String[]> allSentences = new ArrayList<String[]>(100);
	final ArrayList<String> sentence = new ArrayList<String>(40);
	final StringBuilder builder = new StringBuilder();
	
	for(final String token : wordTokens) {
	    sentence.add(token);
	    if(isSentenceBoundary(token)) {
		String[] arrSentence = sentence.toArray(new String[sentence.size()]);
		allSentences.add(arrSentence);
		sentence.clear();
	    }
	}
	
	return allSentences.toArray(new String[allSentences.size()][]);
    }
}
