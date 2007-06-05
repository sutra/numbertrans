/*
 * Created on May 28, 2007
 */
package info.jonclark.corpus.tokenize;

import java.util.ArrayList;
import java.util.Properties;

public class SentenceTokenizer {
	
	public static final String SENTENCE_BOUNDARIES = ".!?\n";

	public SentenceTokenizer(Properties props) {

	}

	private boolean isSentenceBoundary(final String str) {
		if (str.length() != 1) {
			return false;
		} else {
			return (SENTENCE_BOUNDARIES.indexOf(str.charAt(0)) != -1);
		}
	}

	/**
	 * Returns sentences
	 * 
	 * @param text
	 * @return
	 */
	public String[][] tokenizeToSentences(final String[] wordTokens) {
		final ArrayList<String[]> allSentences = new ArrayList<String[]>(100);
		final ArrayList<String> sentence = new ArrayList<String>(40);

		for (final String token : wordTokens) {
			assert token.trim().equals(token) || token.equals("\n") : "Untrimmed token";
			assert !token.trim().equals("") || token.equals("\n") : "Empty token";
			
			if(!token.equals("\n"))
				sentence.add(token);
			
			if (isSentenceBoundary(token) && sentence.size() > 0) {
				String[] arrSentence = sentence.toArray(new String[sentence.size()]);
				allSentences.add(arrSentence);
				sentence.clear();
			}
		}

		return allSentences.toArray(new String[allSentences.size()][]);
	}
}
