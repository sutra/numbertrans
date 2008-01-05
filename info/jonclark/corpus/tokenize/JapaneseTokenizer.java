/*
 * Created on May 26, 2007
 */
package info.jonclark.corpus.tokenize;

import info.jonclark.corpus.languagetools.ja.JapaneseUtils;
import info.jonclark.corpus.languagetools.ja.KanjiKiller;
import info.jonclark.corpus.management.documents.InputDocument;
import info.jonclark.corpus.management.documents.OutputDocument;
import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.iterators.interfaces.ParallelCorpusTransformIterator;
import info.jonclark.corpus.management.runs.ParallelCorpusTransformRun;
import info.jonclark.log.LogUtils;
import info.jonclark.properties.PropertyUtils;
import info.jonclark.util.FileUtils;
import info.jonclark.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Features:<br>
 * <li>Treats all contiguous blocks of katakana as one word
 * <li>Treats all kanji and hiragana as individual tokens
 * <li>Uses a separate English tokenizer for contiguous blocks of Roman
 * characters
 * 
 * @author Jonathan
 */
public class JapaneseTokenizer implements Tokenizer, ParallelCorpusTransformRun {

    private final EnglishTokenizer englishTokenizer;

    // see http://unicode.org/charts/PDF/UFF00.pdf
    public static final int FIRST_FULLWIDTH_ASCII_VARIANT = 0xFF01;
    public static final int LAST_FULLWIDTH_ASCII_VARIANT = 0xFF5E;
    public static final int FIRST_HALFWIDTH_KATAKANA_VARIANT = 0xFF65;
    public static final int LAST_HALFWIDTH_KATAKANA_VARIANT = 0xFF9F;
    public static final int FIRST_HALFWIDTH_CJK_PUNC_VARIANT = 0xFF61;
    public static final int LAST_HALFWIDTH_CJK_PUNC_VARIANT = 0xFF64;
    public static final int FIRST_HIRAGANA = 0x3041;
    public static final int LAST_HIRAGANA = 0x309F;
    public static final int FIRST_KATAKANA = 0x30A0;
    public static final int LAST_KATAKANA = 0x30FF;

    public static final int FIRST_CJK_IDEOGRAPH = 0x4E00;
    public static final int LAST_CJK_IDEOGRAPH = 0x9FBF;
    public static final int FIRST_CJK_IDEOGRAPH_EXT_A = 0x3400;
    public static final int LAST_CJK_IDEOGRAPH_EXT_A = 0x4DBF;
    public static final int FIRST_CJK_IDEOGRAPH_EXT_B = 0x00020000;
    public static final int LAST_CJK_IDEOGRAPH_EXT_B = 0x0002A6DF;

    // see http://unicode.org/charts/PDF/U3000.pdf
    public static final int FIRST_CJK_PUNC = 0x3000;
    public static final int LAST_CJK_PUNC = 0x303F;
    public static final int FIRST_ROMAN_CHAR = 0x0021;
    public static final int LAST_ROMAN_CHAR = 0x007E;

    private final KanjiKiller kanjiKiller;

    private static final Logger log = LogUtils.getLogger();

    private final boolean removeNewLines;
    private final boolean makeKatakanaIntoHiragana;
    private final boolean killKanji;

    private final boolean tokenizeKatakanaChars;
    private final boolean tokenizeHiraganaChars;
    private final boolean tokenizeKanjiChars;

    SentenceTokenizer stok;

    public JapaneseTokenizer(Properties props) throws IOException {
	this.englishTokenizer = new EnglishTokenizer(props);
	this.stok = new SentenceTokenizer(props);

	String jmDictXmlFile = props.getProperty("jmDictXmlFile");
	boolean separateKanjiKillerResults = Boolean.parseBoolean(props.getProperty("separateKanjiKillerResults"));

	this.killKanji = Boolean.parseBoolean(props.getProperty("killKanji"));
	if (killKanji)
	    kanjiKiller = new KanjiKiller(jmDictXmlFile, separateKanjiKillerResults);
	else
	    kanjiKiller = null;

	this.tokenizeKatakanaChars = Boolean.parseBoolean(props.getProperty("tokenizeKatakanaChars"));
	this.tokenizeHiraganaChars = Boolean.parseBoolean(props.getProperty("tokenizeHiraganaChars"));
	this.tokenizeKanjiChars = Boolean.parseBoolean(props.getProperty("tokenizeKanjiChars"));
	this.removeNewLines = Boolean.parseBoolean(props.getProperty("removeNewLines"));
	this.makeKatakanaIntoHiragana = Boolean.parseBoolean(props.getProperty("makeKatakanaIntoHiragana"));
    }

    public JapaneseTokenizer(Properties props, String runName, String corpusName)
	    throws IOException {
	this(props);
    }

    public String[] tokenize(String input) {
	input = StringUtils.whitespaceToSpace(input, removeNewLines);
	input = normalizeChars(input);

	final ArrayList<String> tokens = new ArrayList<String>();

	int nBegin = 0;
	while (nBegin < input.length()) {
	    int nEnd;
	    int codePoint = input.codePointAt(nBegin);

	    if (codePoint >= FIRST_ROMAN_CHAR && codePoint <= LAST_ROMAN_CHAR) {

		// let English tokenizer handle Roman chunks
		nEnd = findEnd(input, nBegin, FIRST_ROMAN_CHAR, LAST_ROMAN_CHAR);
		final String chunk = input.substring(nBegin, nEnd);
		String[] enTokens = englishTokenizer.tokenize(chunk);
		for (final String token : enTokens) {
		    assert !token.trim().equals("") : "Blank token";
		    assert token.trim().equals(token) : "Untrimmed token";
		    tokens.add(token);
		}

	    } else if (codePoint >= FIRST_KATAKANA && codePoint <= LAST_KATAKANA) {

		// leave katakana chunks as one word
		nEnd = findEnd(input, nBegin, FIRST_KATAKANA, LAST_KATAKANA);
		String chunk = input.substring(nBegin, nEnd);
		if (makeKatakanaIntoHiragana)
		    chunk = JapaneseUtils.katakanaToHiragana(chunk);

		addChunk(tokens, chunk, tokenizeKatakanaChars);

	    } else if (codePoint >= FIRST_HIRAGANA && codePoint <= LAST_HIRAGANA) {

		// make hiragana individual tokens
		nEnd = findEnd(input, nBegin, FIRST_HIRAGANA, LAST_HIRAGANA);
		final String chunk = input.substring(nBegin, nEnd);
		addChunk(tokens, chunk, tokenizeHiraganaChars);

	    } else if (isKanji(codePoint)) {

		// make kanji individual tokens
		nEnd = findEndOfKanji(input, nBegin);
		String chunk = input.substring(nBegin, nEnd);

		if (killKanji) {
		    chunk = kanjiKiller.killKanji(chunk);
		    String[] tokenizedChunk = StringUtils.tokenize(chunk);
		    for (final String token : tokenizedChunk)
			addChunk(tokens, token, tokenizeKanjiChars);
		} else {
		    addChunk(tokens, chunk, tokenizeKanjiChars);
		}

	    } else {
		// all other unknown characters will be single tokens
		nEnd = nBegin + 1;
		if (!Character.isWhitespace(input.charAt(nBegin))) {
		    String c = input.charAt(nBegin) + "";
		    if (c.trim().equals("")) {
			log.warning("Character trims to empty?!?! (ignoring) char=" + c);
		    } else {
			assert !Character.isSpaceChar(c.charAt(0)) : "Adding blank token via space char";
			assert c.trim().equals(c) : "Untrimmed chunk";
			tokens.add(c);
		    }
		}

		log.fine("Unknown code point: " + codePoint + " = "
			+ new String(Character.toChars(codePoint)));
	    }

	    assert nBegin != nEnd : "Infinite loop detected due to nBegin == nEnd";
	    nBegin = nEnd;
	}

	return tokens.toArray(new String[tokens.size()]);
    }

    private static void addChunk(ArrayList<String> tokens, String chunk, boolean tokenizeChars) {
	if (tokenizeChars) {
	    for (int i = 0; i < chunk.length(); i++) {
		assert !chunk.trim().equals("") : "Character trims to empty?!?!";
		assert !Character.isSpaceChar(chunk.charAt(i)) : "Adding blank token via space char";
		assert chunk.trim().equals(chunk) : "Untrimmed chunk";
		tokens.add(chunk.charAt(i) + "");
	    }
	} else {
	    assert !chunk.trim().equals("") : "Blank chunk";
	    assert chunk.trim().equals(chunk) : "Untrimmed chunk";
	    tokens.add(chunk);
	}
    }

    /**
         * Guaranteed to return a positive value >= fromIndex and a value at
         * least fromIndex+1
         * 
         * @param str
         * @param fromIndex
         * @param leastCodePoint
         * @param greatestCodePoint
         * @return
         */
    private static int findEnd(final String str, int fromIndex, int leastCodePoint,
	    int greatestCodePoint) {
	// iterate through characters, finding end of contiguous block with
	// chars

	int i = fromIndex;
	int codePoint = str.codePointAt(i);
	while (codePoint >= leastCodePoint && codePoint <= greatestCodePoint) {
	    // first iteration guaranteed not to fail unless the user gave
	    // us a bad from index
	    i++;
	    if (i >= str.length())
		break;
	    codePoint = str.codePointAt(i);
	}

	return i;
    }

    public static boolean isKanji(final int codePoint) {
	return (codePoint >= FIRST_CJK_IDEOGRAPH && codePoint <= LAST_CJK_IDEOGRAPH)
		|| (codePoint >= FIRST_CJK_IDEOGRAPH_EXT_A && codePoint <= LAST_CJK_IDEOGRAPH_EXT_A)
		|| (codePoint >= FIRST_CJK_IDEOGRAPH_EXT_B && codePoint <= LAST_CJK_IDEOGRAPH_EXT_B);
    }

    private static int findEndOfKanji(final String str, int fromIndex) {
	// iterate through characters, finding end of contiguous block with
	// chars

	int i = fromIndex;
	int codePoint = str.codePointAt(i);
	while (isKanji(codePoint)) {
	    // first iteration guaranteed not to fail unless the user gave
	    // us a bad from index
	    i++;
	    if (i < str.length())
		break;
	    codePoint = str.codePointAt(i);
	}

	return i;
    }

    /**
         * Convert full-width and half-width Roman characters to ASCII
         * 
         * @param input
         */
    public static String normalizeChars(final String input) {

	char[] arr = input.toCharArray();
	for (int i = 0; i < input.length(); i++) {
	    int codePoint = input.codePointAt(i);

	    if (codePoint == '\t')
		arr[i] = ' ';

	    // normalize full-width Roman chars
	    if (codePoint >= FIRST_FULLWIDTH_ASCII_VARIANT
		    && codePoint <= LAST_FULLWIDTH_ASCII_VARIANT) {
		arr[i] = (char) (codePoint - FIRST_FULLWIDTH_ASCII_VARIANT + 0x0021);
	    }

	    // normalize half-width katakana
	    if (codePoint >= FIRST_HALFWIDTH_KATAKANA_VARIANT
		    && codePoint <= LAST_HALFWIDTH_KATAKANA_VARIANT) {
		arr[i] = (char) (codePoint - FIRST_HALFWIDTH_KATAKANA_VARIANT + 0x30Fb);
	    }

	    // normalize half-width CJK punctuation (re-normalized in next
	    // step)
	    if (codePoint >= FIRST_HALFWIDTH_CJK_PUNC_VARIANT
		    && codePoint <= LAST_HALFWIDTH_CJK_PUNC_VARIANT) {
		switch (codePoint) {
		case 0xFF61:
		    codePoint = 0x3002;
		    break;
		case 0xFF62:
		    codePoint = 0x300C;
		    break;
		case 0xFF63:
		    codePoint = 0x300D;
		    break;
		case 0xFF64:
		    codePoint = 0x3001;
		    break;
		}
	    }

	    // normalize punctuation
	    if (codePoint >= FIRST_CJK_PUNC && codePoint <= LAST_CJK_PUNC) {
		switch (codePoint) {
		case 0x3001:
		    arr[i] = ',';
		    break;
		case 0x3002:
		    arr[i] = '.';
		    break;
		case 0x3003:
		    arr[i] = '"';
		    break;
		case 0x3008:
		case 0x300A:
		case 0x300C:
		case 0x300E:
		case 0x3010:
		case 0x3014:
		case 0x3016:
		case 0x3018:
		case 0x301A:
		case 0x301D:
		    arr[i] = '"';
		    break;
		case 0x3009:
		case 0x300B:
		case 0x300D:
		case 0x300F:
		case 0x3011:
		case 0x3015:
		case 0x3017:
		case 0x3019:
		case 0x301B:
		case 0x301E:
		    arr[i] = '"';
		    break;
		}
	    }

	    // normalize katakana word separator
	    if (codePoint == '・') {
		arr[i] = ' ';
	    }
	} // end while

	return new String(arr);
    }

    public void printKanjiKillerStats() {
	if (killKanji)
	    kanjiKiller.printStats();
    }

    public void processCorpus(ParallelCorpusTransformIterator iterator) throws CorpusManException {
	try {
	    while (iterator.hasNext()) {
		iterator.next();

		InputDocument inE = iterator.getInputDocumentE();
		InputDocument inF = iterator.getInputDocumentF();
		OutputDocument outE = iterator.getOutputDocumentE();
		OutputDocument outF = iterator.getOutputDocumentF();

		String ja = inF.getWholeFile();
		String[][] tokenized = stok.tokenizeToSentences(this.tokenize(ja));
		String strTokenized = StringUtils.untokenize(tokenized, false);
		outF.println(strTokenized);

		String en = inE.getWholeFile();
		tokenized = stok.tokenizeToSentences(this.englishTokenizer.tokenize(en));
		strTokenized = StringUtils.untokenize(tokenized, false);
		outE.println(strTokenized);

		inE.close();
		inF.close();
		outE.close();
		outF.close();
	    }

	    iterator.finish();
	} catch (IOException e) {
	    throw new CorpusManException(e);
	}
    }

    /**
         * This program removes all previous file extentions and replaces with
         * the specified extension
         */
    public static void main(String... args) throws Exception {
	if (args.length != 3) {
	    System.err.println("Usage: program <properties_file> <input_file_wildcard> <output_ext>");
	    System.exit(1);
	}

	Properties props = PropertyUtils.getProperties(args[0]);
	JapaneseTokenizer jtok = new JapaneseTokenizer(props);
	SentenceTokenizer stok = new SentenceTokenizer(props);

	System.out.println("Finding files...");
	File[] files = FileUtils.getFilesFromWildcard(args[1]);
	System.out.println(files.length + " files found.");

	String outExt = args[2];

	for (final File file : files) {
	    String input = FileUtils.getFileAsString(file);
	    String[][] tokenized = stok.tokenizeToSentences(jtok.tokenize(input));
	    String strTokenized = StringUtils.untokenize(tokenized, false);

	    String outName = StringUtils.substringBefore(file.getName(), ".") + outExt;
	    FileUtils.saveFileFromString(new File(file.getParentFile(), outName), strTokenized);
	}

	jtok.printKanjiKillerStats();

	System.out.println("Done.");
    }
}
