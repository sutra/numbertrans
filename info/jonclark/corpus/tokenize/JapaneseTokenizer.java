/*
 * Created on May 26, 2007
 */
package info.jonclark.corpus.tokenize;

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
public class JapaneseTokenizer {

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

    private static final Logger log = LogUtils.getLogger();

    public JapaneseTokenizer(Properties props) throws IOException {
	englishTokenizer = new EnglishTokenizer(props);
    }

    public ArrayList<String> tokenize(final String input) {
	String str = normalizeChars(input);

	final ArrayList<String> tokens = new ArrayList<String>();

	// what kind of character are we currently on?
	// where's the end of this chunk of this character type?

	int nBegin = 0;
	while (nBegin < input.length()) {
	    int nEnd;
	    int codePoint = input.codePointAt(nBegin);

	    if (codePoint >= FIRST_ROMAN_CHAR && codePoint <= LAST_ROMAN_CHAR) {

		// let English tokenizer handle Roman chunks
		nEnd = findEnd(str, nBegin, FIRST_ROMAN_CHAR, LAST_ROMAN_CHAR);
		final String chunk = input.substring(nBegin, nEnd);
		String[] enTokens = englishTokenizer.tokenize(chunk);
		for (final String token : enTokens)
		    tokens.add(token);

	    } else if (codePoint >= FIRST_KATAKANA && codePoint <= LAST_KATAKANA) {

		// leave katakana chunks as one word
		nEnd = findEnd(str, nBegin, FIRST_KATAKANA, LAST_KATAKANA);
		final String chunk = input.substring(nBegin, nEnd);
		tokens.add(chunk);

	    } else if (codePoint >= FIRST_HIRAGANA && codePoint <= LAST_HIRAGANA) {

		// make hiragana individual tokens
		nEnd = findEnd(str, nBegin, FIRST_HIRAGANA, LAST_HIRAGANA);
		final String chunk = input.substring(nBegin, nEnd);
		for (int i = 0; i < chunk.length(); i++)
		    tokens.add(chunk.charAt(i) + "");

	    } else if (isKanji(codePoint)) {

		// make kanji individual tokens
		nEnd = findEndOfKanji(str, nBegin);
		final String chunk = input.substring(nBegin, nEnd);
		for (int i = 0; i < chunk.length(); i++)
		    tokens.add(chunk.charAt(i) + "");

	    } else {
		// all other unknown characters will be single tokens
		nEnd = nBegin + 1;
		tokens.add(input.charAt(nBegin) + "");
		
		log.fine("Unknown code point: " + codePoint + " = "
			+ new String(Character.toChars(codePoint)));
	    }

	    assert nBegin != nEnd : "Infinite loop detected due to nBegin == nEnd";
	    nBegin = nEnd;
	}

	return tokens;
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
	int codePoint = leastCodePoint;
	while (i < str.length() && codePoint >= leastCodePoint && codePoint <= greatestCodePoint) {
	    // first iteration guaranteed not to fail unless the user gave
	    // us a bad from index
	    codePoint = str.codePointAt(i);
	    i++;
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

	int i = 0;
	int codePoint = FIRST_CJK_IDEOGRAPH;
	while (i < str.length() && isKanji(codePoint)) {
	    // first iteration guaranteed not to fail unless the user gave
	    // us a bad from index
	    codePoint = str.codePointAt(i);
	    i++;
	}

	return i;
    }

    /**
         * Convert full-width and half-width Roman characters to ASCII
         * 
         * @param input
         */
    public static String normalizeChars(final String input) {

	final char[] arr = input.toCharArray();
	for (int i = 0; i < arr.length; i++) {

	    // normalize full-width Roman chars
	    if (arr[i] >= FIRST_FULLWIDTH_ASCII_VARIANT && arr[i] <= LAST_FULLWIDTH_ASCII_VARIANT) {
		arr[i] = (char) (arr[i] - FIRST_FULLWIDTH_ASCII_VARIANT + 0x0021);
	    }

	    // normalize half-width katakana
	    if (arr[i] >= FIRST_HALFWIDTH_KATAKANA_VARIANT
		    && arr[i] <= LAST_HALFWIDTH_KATAKANA_VARIANT) {
		arr[i] = (char) (arr[i] - FIRST_HALFWIDTH_KATAKANA_VARIANT + 0x30Fb);
	    }

	    // normalize half-width CJK punctuation (re-normalized in next
	    // step)
	    if (arr[i] >= FIRST_HALFWIDTH_CJK_PUNC_VARIANT
		    && arr[i] <= LAST_HALFWIDTH_CJK_PUNC_VARIANT) {
		switch (arr[i]) {
		case 0xFF61:
		    arr[i] = 0x3002;
		    break;
		case 0xFF62:
		    arr[i] = 0x300C;
		    break;
		case 0xFF63:
		    arr[i] = 0x300D;
		    break;
		case 0xFF64:
		    arr[i] = 0x3001;
		    break;
		}
	    }

	    // normalize punctuation
	    if (arr[i] >= FIRST_CJK_PUNC && arr[i] <= LAST_CJK_PUNC) {
		switch (arr[i]) {
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
	    if (arr[i] == '・') {
		arr[i] = ' ';
	    }
	} // end while

	return new String(arr);
    }

    public static void main(String... args) throws Exception {
	if (args.length != 3) {
	    System.err.println("Usage: program <properties_file> <input_file> <output_file>");
	    System.exit(1);
	}

	Properties props = PropertyUtils.getProperties(args[0]);
	JapaneseTokenizer tok = new JapaneseTokenizer(props);
	String input = FileUtils.getFileAsString(new File(args[1]));
	String norm = normalizeChars(input);
	FileUtils.saveFileFromString(new File(args[2]), norm);
	String tokenized = StringUtils.untokenize(tok.tokenize(input));
	FileUtils.saveFileFromString(new File(args[2]), tokenized);
    }
}
