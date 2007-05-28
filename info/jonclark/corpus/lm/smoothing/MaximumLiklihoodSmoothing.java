/*
 * Created on Jan 18, 2007
 */
package info.jonclark.corpus.lm.smoothing;

import info.jonclark.corpus.lm.LanguageModel;
import info.jonclark.lang.TokenArray;
import info.jonclark.log.LogUtils;
import info.jonclark.log.VarLogger;

/**
 * This "smoothing technique" is actually the equivalent of no smoothing at all.
 * It will return a zero probability for any sentences that contain unseen
 * history phrases.
 * <p>
 * This technique was implemented from "An Empirical Study of Smoothing
 * Techniques for Language Modeling" by Stanley F. Chen (August 1998) pp.5-6
 */
public class MaximumLiklihoodSmoothing implements LanguageModelSmoothing {

    protected static final VarLogger log = LogUtils.getLogger();
    protected LanguageModel parent = null;

    public MaximumLiklihoodSmoothing() {
    }

    /**
         * Initialization to be called by the parent language model.
         * 
         * @param parent
         * @param nGramMax
         */
    public void setParent(LanguageModel parent) {
	this.parent = parent;
    }

    /**
         * Get the score (probability) for a sentence (array of tokens). The
         * sentence must begin with the token LanguageModel.START_SENTENCE and
         * end with the token LanguageModel.END_SENTENCE.
         */
    public double getSentenceProbability(TokenArray sentence) {
	assert parent != null : "setParent() must be called prior to this method.";
	assert sentence.get(0).equals(LanguageModel.BEGIN_SENTENCE) : "Expected"
		+ " sentence to begin with LanguageModel.BEGIN_SENTENCE, but instead found: "
		+ sentence.get(0);
	assert sentence.getLastToken().equals(LanguageModel.END_SENTENCE) : "Expected"
		+ " sentence to end with LanguageModel.END_SENTENCE, but instead found: "
		+ sentence.getLastToken();

	double totalProb = 1.0;

	for (int i = 1; i < sentence.length(); i++) {
	    // make sure we don't exceed array bounds
	    int nFirst = i - parent.getNGramMax() + 1;
	    if (nFirst < 0)
		nFirst = 0;
	    TokenArray phrase = sentence.subarray(nFirst, i);

	    totalProb *= getWordProbability(phrase);
	}

	return totalProb;
    }

    /**
         * Get the probability of a word, given the word and its context. The
         * probability of last token (n) in the phrase will be evaluated while
         * tokens 0 through n-1 will be used as context.
         * 
         * @param phrase
         * @return
         */
    public double getWordProbability(TokenArray phrase) {
	assert parent != null : "setParent() must be called prior to this method.";
	
	double phraseCount = parent.countOccurances(phrase);
	double historyCount = parent.countOccurances(phrase.cutTokensFromEnd(1));
	double totalProb = historyCount == 0 ? 0 : phraseCount / historyCount;
	log.info("p({0}|{1}) = {2}", phrase.getLastToken(), phrase.cutTokensFromEnd(1), totalProb);

	return totalProb;
    }

    public String toString() {
	return "Maximum Liklihood Smoothing";
    }
}
