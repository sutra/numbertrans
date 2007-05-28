/*
 * Created on May 26, 2007
 */
package info.jonclark.corpus.tokenize;

import java.util.ArrayList;
import java.util.Properties;

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
    public static final char FIRST_FULLWIDTH_ASCII_VARIANT = 0xFF01;
    public static final char LAST_FULLWIDTH_ASCII_VARIANT = 0xFF5E;

    public static final char FIRST_HALFWIDTH_KATAKANA_VARIANT = 0xFF65;
    public static final char LAST_HALFWIDTH_KATAKANA_VARIANT = 0xFF9F;

    public static final char FIRST_HALFWIDTH_CJK_PUNC_VARIANT = 0xFF61;
    public static final char LAST_HALFWIDTH_CJK_PUNC_VARIANT = 0xFF64;

    public static final char FIRST_HIRAGANA = 0x3041;
    public static final char LAST_HIRAGANA = 0x309F;

    public static final char FIRST_KATAKANA = 0x30A0;
    public static final char LAST_KATAKANA = 0x30FF;
    
    public static final char FIRST_CJK_IDEOGRAPH = 0x30A0;
    public static final char LAST_CJK_IDEOGRAPH = 0x30FF;

    // see http://unicode.org/charts/PDF/U3000.pdf
    public static final char FIRST_CJK_PUNC = 0x3000;
    public static final char LAST_CJK_PUNC = 0x303F;

    public static final char FIRST_ROMAN_CHAR = 0x0021;
    public static final char LAST_ROMAN_CHAR = 0x007E;

    public JapaneseTokenizer(Properties props) {
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
	    char c = input.charAt(nBegin);

	    if (c >= FIRST_ROMAN_CHAR && c <= LAST_ROMAN_CHAR) {

		nEnd = findEnd(str, nBegin, FIRST_ROMAN_CHAR, LAST_ROMAN_CHAR);
		final String chunk = input.substring(nBegin, nEnd);
		String[] enTokens = englishTokenizer.tokenize(chunk);
		for (final String token : enTokens)
		    tokens.add(token);

	    } else if (c >= FIRST_KATAKANA && c <= LAST_KATAKANA) {

	    } else if (c >= FIRST_HIRAGANA && c <= LAST_HIRAGANA) {

	    } else if (false /* Kanji */) {

	    } else {
		// unknown character type!
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

	int i = 0;
	char c = (char) leastCodePoint;
	while (i < str.length() && c >= leastCodePoint && c <= greatestCodePoint) {
	    // first iteration guaranteed not to fail unless the user gave
	    // us a bad from index
	    c = str.charAt(i);
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
	}

	return new String(arr);
    }
}
