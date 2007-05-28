/*
 * Created on Jan 18, 2007
 */
package info.jonclark.testcases;

import java.io.FileInputStream;
import java.util.List;

import info.jonclark.corpus.CorpusUtils;
import info.jonclark.corpus.lm.LanguageModel;
import info.jonclark.corpus.lm.SuffixArrayLanguageModel;
import info.jonclark.corpus.lm.smoothing.AdditiveSmoothing;
import info.jonclark.corpus.lm.smoothing.MaximumLiklihoodSmoothing;
import info.jonclark.lang.TokenArray;
import info.jonclark.log.LogUtils;
import info.jonclark.log.VarLogger;
import info.jonclark.util.FormatUtils;
import junit.framework.TestCase;

public class SuffixArrayLanguageModelTest extends TestCase {

    private final static VarLogger log = LogUtils.getLogger();

    /**
         * Test case from pp.5-6 of "An Empirical Study of Smoothing Techniques
         * for Language Modeling"
         * 
         * @throws Exception
         */
    public void testBigram() throws Exception {
	SuffixArrayLanguageModel lm = new SuffixArrayLanguageModel(new FileInputStream(
		"conf/corpus.txt"), new MaximumLiklihoodSmoothing(), 2, true);
	List<String> tokens = CorpusUtils.tokenize("John read a book");
	tokens.add(0, LanguageModel.BEGIN_SENTENCE);
	tokens.add(tokens.size(), LanguageModel.END_SENTENCE);
	TokenArray sentence = new TokenArray(tokens.toArray(new String[tokens.size()]));

	// test maximum liklihood
	double prob = lm.getSentenceProbability(sentence);
	log.info("Overall probability for maximum liklihood: " + FormatUtils.formatDouble2(prob));
	assertEquals(2d / 36d, prob);

	// test additive smoothing
	lm.setSmoothingTechnique(new AdditiveSmoothing(1.0));
	prob = lm.getSentenceProbability(sentence);
	log.info("Overall probability for additive (1.0): " + prob);
	assertEquals(48d / 397488d, prob);
    }
}
