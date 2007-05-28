/*
 * Created on May 26, 2007
 */
package info.jonclark.util;

import java.util.HashMap;

public class HtmlUtils {

    /**
         * Removes JavaScript portions from pages
         * 
         * @param html
         */
    public static String removeScript(final String html) {
	return removeTextBetween(html, "<script", "</script>");
    }

    public static String removeTextBetween(final String input, String startMarker, String endMarker) {
	startMarker = startMarker.toLowerCase();
	endMarker = endMarker.toLowerCase();
	final String searchable = input.toLowerCase();
	final StringBuilder builder = new StringBuilder();

	int nTagBegin = searchable.indexOf(startMarker);
	if (nTagBegin != -1) {
	    int nTextBegin = 0;
	    builder.append(input.substring(nTextBegin, nTagBegin));

	    nTextBegin = searchable.indexOf(endMarker, nTagBegin) + 1;
	    nTagBegin = searchable.indexOf(startMarker, nTextBegin);
	    while (nTextBegin != 0 && nTagBegin != -1) {
		builder.append(input.substring(nTextBegin, nTagBegin) + " ");
		nTextBegin = searchable.indexOf(endMarker, nTagBegin) + 1;
		nTagBegin = searchable.indexOf(startMarker, nTextBegin);
	    }

	    if (nTextBegin != 0) {
		builder.append(input.substring(nTextBegin, input.length()));
	    }

	    return builder.toString();
	} else {
	    return input;
	}
    }

    public static String removeAllTags(final String html) {
	final StringBuilder builder = new StringBuilder();

	int nTagBegin = html.indexOf('<');
	if (nTagBegin != -1) {
	    int nTextBegin = 0;
	    builder.append(html.substring(nTextBegin, nTagBegin));

	    nTextBegin = html.indexOf('>', nTagBegin) + 1;
	    nTagBegin = html.indexOf('<', nTextBegin);
	    while (nTextBegin != 0 && nTagBegin != -1) {
		builder.append(html.substring(nTextBegin, nTagBegin) + " ");
		nTextBegin = html.indexOf('>', nTagBegin) + 1;
		nTagBegin = html.indexOf('<', nTextBegin);
	    }

	    if (nTextBegin != 0) {
		builder.append(html.substring(nTextBegin, html.length()));
	    }

	    return builder.toString();
	} else {
	    return html;
	}
    }

    public static String extractNonNestedTag(final String html, final String tagName) {
	final String openTag = "<" + tagName + ">";
	final String closeTag = "</" + tagName + ">";

	int nBegin = html.indexOf(openTag);
	if (nBegin == -1)
	    nBegin = html.indexOf(openTag.toUpperCase());
	if (nBegin == -1)
	    nBegin = html.toLowerCase().indexOf(openTag);

	if (nBegin == -1) {
	    return null;
	} else {
	    nBegin += openTag.length();

	    int nEnd = html.indexOf(closeTag);
	    if (nEnd == -1)
		nEnd = html.indexOf(closeTag.toUpperCase());
	    if (nEnd == -1)
		nEnd = html.toLowerCase().indexOf(closeTag);
	    if (nEnd == -1)
		nEnd = html.length();

	    return html.substring(nBegin, nEnd);
	}
    }

    /**
         * @param html
         * @param tagBegin
         *                Note that this should NOT include the opening angle
         *                brace<br>
         *                e.g. "body bgcolor="
         * @return
         */
    public static String extractFirstOf(final String html, final String tagBeginPart,
	    final String tagClose, boolean caseSensitive) {

	int nBegin = html.indexOf(tagBeginPart);

	if (!caseSensitive) {
	    if (nBegin == -1)
		nBegin = html.indexOf(tagBeginPart.toUpperCase());
	    if (nBegin == -1)
		nBegin = html.toLowerCase().indexOf(tagBeginPart.toLowerCase());
	}

	if (nBegin == -1) {
	    return html;
	} else {

	    int nEnd = html.indexOf(tagClose, nBegin);

	    if (!caseSensitive) {
		if (nEnd == -1)
		    nEnd = html.indexOf(tagClose.toUpperCase(), nBegin);
		if (nEnd == -1)
		    nEnd = html.toLowerCase().indexOf(tagClose.toLowerCase(), nBegin);
	    }
	    if (nEnd == -1)
		nEnd = html.length();

	    return html.substring(nBegin, nEnd);
	}
    }

    public static String extractBody(final String html) {
	return extractFirstOf(html, "<body", "</body>", false);
    }

    private static final HashMap<String, String> ESCAPE_SEQ;
    static {
	ESCAPE_SEQ = new HashMap<String, String>();

	ESCAPE_SEQ.put("sp", " ");
	ESCAPE_SEQ.put("blank", " ");
	ESCAPE_SEQ.put("excl", "!");
	ESCAPE_SEQ.put("quot", "\"");
	ESCAPE_SEQ.put("num", "#");
	ESCAPE_SEQ.put("dollar", "$");
	ESCAPE_SEQ.put("percnt", "%");
	ESCAPE_SEQ.put("amp", "");
	ESCAPE_SEQ.put("apos", "'");
	ESCAPE_SEQ.put("lpar", "(");
	ESCAPE_SEQ.put("rpar", ")");
	ESCAPE_SEQ.put("ast", "*");
	ESCAPE_SEQ.put("plus", "+");
	ESCAPE_SEQ.put("comma", ",");
	ESCAPE_SEQ.put("hyphen", "-");
	ESCAPE_SEQ.put("minus", "-");
	ESCAPE_SEQ.put("dash", "-");
	ESCAPE_SEQ.put("period", ".");
	ESCAPE_SEQ.put("sol", "/");
	ESCAPE_SEQ.put("colon", ":");
	ESCAPE_SEQ.put("semi", "");
	ESCAPE_SEQ.put("lt", "<");
	ESCAPE_SEQ.put("equals", "=");
	ESCAPE_SEQ.put("gt", ">");
	ESCAPE_SEQ.put("quest", "?");
	ESCAPE_SEQ.put("commat", "@");
	ESCAPE_SEQ.put("lsqb", "[");
	ESCAPE_SEQ.put("bsol", "\\");
	ESCAPE_SEQ.put("rsqb", "]");
	ESCAPE_SEQ.put("caret", "^");
	ESCAPE_SEQ.put("lowbar", "_");
	ESCAPE_SEQ.put("one", "`");
	ESCAPE_SEQ.put("lcub", "{");
	ESCAPE_SEQ.put("verbar", "|");
	ESCAPE_SEQ.put("rcub", "}");
	ESCAPE_SEQ.put("tilde", "~");
	ESCAPE_SEQ.put("sim", "~");
	ESCAPE_SEQ.put("nbsp", " ");
	ESCAPE_SEQ.put("iexcl", "¡");
	ESCAPE_SEQ.put("cent", "¢");
	ESCAPE_SEQ.put("pound", "£");
	ESCAPE_SEQ.put("curren", "¤");
	ESCAPE_SEQ.put("yen", "¥");
	ESCAPE_SEQ.put("brkbar", "¦");
	ESCAPE_SEQ.put("sect", "§");
	ESCAPE_SEQ.put("uml", "¨");
	ESCAPE_SEQ.put("die", "¨");
	ESCAPE_SEQ.put("copy", "©");
	ESCAPE_SEQ.put("ordf", "ª");
	ESCAPE_SEQ.put("laquo", "«");
	ESCAPE_SEQ.put("not", "¬");
	ESCAPE_SEQ.put("shy", "­");
	ESCAPE_SEQ.put("reg", "®");
	ESCAPE_SEQ.put("macr", "¯");
	ESCAPE_SEQ.put("hibar", "¯");
	ESCAPE_SEQ.put("deg", "°");
	ESCAPE_SEQ.put("plusmn", "±");
	ESCAPE_SEQ.put("sup2", "²");
	ESCAPE_SEQ.put("sup3", "³");
	ESCAPE_SEQ.put("acute", "´");
	ESCAPE_SEQ.put("micro", "µ");
	ESCAPE_SEQ.put("para", "¶");
	ESCAPE_SEQ.put("middot", "·");
	ESCAPE_SEQ.put("cedil", "¸");
	ESCAPE_SEQ.put("sup1", "¹");
	ESCAPE_SEQ.put("ordm", "º");
	ESCAPE_SEQ.put("raquo", "»");
	ESCAPE_SEQ.put("frac14", "¼");
	ESCAPE_SEQ.put("frac12", "½");
	ESCAPE_SEQ.put("half", "½");
	ESCAPE_SEQ.put("frac34", "¾");
	ESCAPE_SEQ.put("iquest", "¿");
	ESCAPE_SEQ.put("Agrave", "À");
	ESCAPE_SEQ.put("Aacute", "Á");
	ESCAPE_SEQ.put("Acirc", "Â");
	ESCAPE_SEQ.put("Atilde", "Ã");
	ESCAPE_SEQ.put("Auml", "Ä");
	ESCAPE_SEQ.put("Aring", "Å");
	ESCAPE_SEQ.put("angst", "Å");
	ESCAPE_SEQ.put("AElig", "Æ");
	ESCAPE_SEQ.put("Ccedil", "Ç");
	ESCAPE_SEQ.put("Egrave", "È");
	ESCAPE_SEQ.put("Eacute", "É");
	ESCAPE_SEQ.put("Ecirc", "Ê");
	ESCAPE_SEQ.put("Euml", "Ë");
	ESCAPE_SEQ.put("Igrave", "Ì");
	ESCAPE_SEQ.put("Iacute", "Í");
	ESCAPE_SEQ.put("Icirc", "Î");
	ESCAPE_SEQ.put("Iuml", "Ï");
	ESCAPE_SEQ.put("ETH", "Ð");
	ESCAPE_SEQ.put("Dstrok", "Ð");
	ESCAPE_SEQ.put("Ntilde", "Ñ");
	ESCAPE_SEQ.put("Ograve", "Ò");
	ESCAPE_SEQ.put("Oacute", "Ó");
	ESCAPE_SEQ.put("Ocirc", "Ô");
	ESCAPE_SEQ.put("Otilde", "Õ");
	ESCAPE_SEQ.put("Ouml", "Ö");
	ESCAPE_SEQ.put("times", "×");
	ESCAPE_SEQ.put("Oslash", "Ø");
	ESCAPE_SEQ.put("Ugrave", "Ù");
	ESCAPE_SEQ.put("Uacute", "Ú");
	ESCAPE_SEQ.put("Ucirc", "Û");
	ESCAPE_SEQ.put("Uuml", "Ü");
	ESCAPE_SEQ.put("Yacute", "Ý");
	ESCAPE_SEQ.put("THORN", "Þ");
	ESCAPE_SEQ.put("szlig", "ß");
	ESCAPE_SEQ.put("agrave", "à");
	ESCAPE_SEQ.put("aacute", "á");
	ESCAPE_SEQ.put("acirc", "â");
	ESCAPE_SEQ.put("atilde", "ã");
	ESCAPE_SEQ.put("auml", "ä");
	ESCAPE_SEQ.put("aring", "å");
	ESCAPE_SEQ.put("aelig", "æ");
	ESCAPE_SEQ.put("ccedil", "ç");
	ESCAPE_SEQ.put("egrave", "è");
	ESCAPE_SEQ.put("eacute", "é");
	ESCAPE_SEQ.put("ecirc", "ê");
	ESCAPE_SEQ.put("euml", "ë");
	ESCAPE_SEQ.put("igrave", "ì");
	ESCAPE_SEQ.put("iacute", "í");
	ESCAPE_SEQ.put("icirc", "î");
	ESCAPE_SEQ.put("iuml", "ï");
	ESCAPE_SEQ.put("eth", "ð");
	ESCAPE_SEQ.put("ntilde", "ñ");
	ESCAPE_SEQ.put("ograve", "ò");
	ESCAPE_SEQ.put("oacute", "ó");
	ESCAPE_SEQ.put("ocirc", "ô");
	ESCAPE_SEQ.put("otilde", "õ");
	ESCAPE_SEQ.put("ouml", "ö");
	ESCAPE_SEQ.put("divide", "÷");
	ESCAPE_SEQ.put("oslash", "ø");
	ESCAPE_SEQ.put("ugrave", "ù");
	ESCAPE_SEQ.put("uacute", "ú");
	ESCAPE_SEQ.put("ucirc", "û");
	ESCAPE_SEQ.put("uuml", "ü");
	ESCAPE_SEQ.put("yacute", "ý");
	ESCAPE_SEQ.put("thorn", "þ");
	ESCAPE_SEQ.put("yuml", "ÿ");
    }

    public static String unescape(String html) {
	int nBegin = html.indexOf('&');
	while (nBegin != -1) {
	    int nEnd = html.indexOf(";", nBegin);
	    if (nEnd == -1 || nEnd - nBegin > 6) {
		// this match is too far away
		nBegin = html.indexOf('&', nBegin + 1);
	    } else {
		final String old = html.substring(nBegin, nEnd + 1);
		final String sequence = html.substring(nBegin + 1, nEnd);
		String replacement;

		if (sequence.startsWith("#")) {
		    int n = Integer.parseInt(sequence.substring(1));
		    replacement = new String(Character.toChars(n));
		} else {
		    replacement = ESCAPE_SEQ.get(sequence);
		    if (replacement == null)
			replacement = old;
		}

		// TODO: Make this faster by using a StringBuilder
		html = StringUtils.replaceFast(html, old, replacement);

		// compensate for the difference in lengths
		nEnd -= old.length() - replacement.length();

		nBegin = html.indexOf('&', nEnd);
	    }
	}

	return html;
    }

    public static void main(String... args) {
	System.out.println(unescape("hi&nbsp;&#0123;&quot;"));
	System.out.println(removeTextBetween("hi (junk) hello", "(", ")"));
    }
}
