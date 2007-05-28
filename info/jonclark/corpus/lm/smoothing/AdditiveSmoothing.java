/*
 * Created on Jan 18, 2007
 */
package info.jonclark.corpus.lm.smoothing;

import info.jonclark.corpus.lm.LanguageModel;
import info.jonclark.lang.TokenArray;
import info.jonclark.log.LogUtils;
import info.jonclark.log.VarLogger;

/**
 * This technique smooths the language model by simply adding a given value to
 * the number of times each word has been seen. This avoids zero probabilities
 * for unseen phrases and balances the model in general.
 * <p>
 * Implemented from "An Empirical Study of Smoothing Techniques for Language
 * Modeling" by Stanley F. Chen (August 1998) p.8
 */
public class AdditiveSmoothing extends MaximumLiklihoodSmoothing {

    private static final VarLogger log = LogUtils.getLogger();
    private final double phraseAdd;
    private double historyAdd;

    /**
         * @param add
         *                The amount that will be added to the number of times
         *                each word has been encountered. This value is
         *                typically 0 < add <= 1.
         */
    public AdditiveSmoothing(double add) {
	this.phraseAdd = add;
    }

    /**
         * Get the probability of a word, given the word and its context. The
         * probability of last token (n) in the phrase will be evaluated while
         * tokens 0 through n-1 will be used as context.
         * 
         * @param phrase
         * @return
         */
    @Override
    public double getWordProbability(TokenArray phrase) {
	assert parent != null : "setParent() must be called prior to this method.";

	double phraseCount = parent.countOccurances(phrase) + phraseAdd;
	double historyCount = parent.countOccurances(phrase.cutTokensFromEnd(1)) + historyAdd;
	double totalProb = historyCount == 0 ? 0 : phraseCount / historyCount;
	log.info("p({0}|{1}) = {2}", phrase.getLastToken(), phrase.cutTokensFromEnd(1), totalProb);

	return totalProb;
    }
    
    @Override
    public void setParent(LanguageModel parent) {
	super.setParent(parent);
	historyAdd = phraseAdd * parent.getVocabularySize();	
    }

    public String toString() {
	return "Additive Smoothing (add=" + phraseAdd + ")";
    }
}
