/*
 * Created on May 28, 2007
 */
package info.jonclark.corpus.tokenize;

import java.util.HashSet;
import java.util.Properties;

public class SentenceTokenizer {
    // need an abbreviation list
    
    private final HashSet<String> abbreviations = new HashSet<String>();
    
    public SentenceTokenizer(Properties props) {
	
    }
    
    /**
     * Returns sentences
     * 
     * @param text
     * @return
     */
    public String[] tokenizeToSentences(final String[] wordTokens) {
	return null;
    }
}
