/*
 * Created on Jan 12, 2007
 */
package info.jonclark.fun;

import info.jonclark.corpus.CorpusUtils;

import java.util.List;
import java.util.Random;

public class WordMutator {
    private static Random rand = new Random();

    /**
         * Mutate a word in line with a study that shows the human mind can
         * interpret a word as long as it contains the right letters and the
         * first and last letters are in the correct position.
         * 
         * @param word
         * @return
         */
    public static String mutateWord(String word) {
	if (word.length() <= 3) {
	    return word;
	} else {
	    // randomly swap some letters around
	    char[] buf = word.toCharArray();
	    for (int i = 1; i < buf.length - 1; i++) {
		int r1 = rand.nextInt(buf.length - 2) + 1;
		int r2 = rand.nextInt(buf.length - 2) + 1;

		// swap positions r1 and r2
		char c = buf[r1];
		buf[r1] = buf[r2];
		buf[r2] = c;
	    }
	    return new String(buf);
	}
    }

    public static String mutateSentence(final String originalSentence) {
	List<String> tokenList = CorpusUtils.tokenize(originalSentence);
	String[] tokens = tokenList.toArray(new String[tokenList.size()]);
	for (int i = 0; i < tokens.length; i++)
	    tokens[i] = mutateWord(tokens[i]);
	
	String mutantSentence = CorpusUtils.untokenize(tokens);
	return mutantSentence;
    }

    public static void main(String... args) {
	System.out.println(mutateSentence("yeaux. hi there. somebody in little rock thinks you're pretty cool. that somebody might be excited to see you tomorrow, too. - somebody"));
    }
}
