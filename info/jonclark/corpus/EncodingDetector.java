package info.jonclark.corpus;

import info.jonclark.lang.Mutable;
import info.jonclark.util.FileUtils;
import info.jonclark.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;

public class EncodingDetector {

    public static final int ALL = 0;
    public static final int JAPANESE = 1;
    public static final int CHINESE = 2;
    public static final int SIMPLIFIED_CHINESE = 3;
    public static final int TRADITIONAL_CHINESE = 4;
    public static final int KOREAN = 5;

    /**
         * Searches through an HTML file for a charset type. If none is found,
         * returns. It is recommended that the user try an autodetection method
         * (also included in this class) as a fallback.
         * 
         * @throws IOException
         */
    public static Charset getEncodingFromHtml(File file) throws IOException {
	String html = FileUtils.getFileAsString(file);
	String head = "<meta http-equiv=\"Content-Type\"";
	String inside = StringUtils.substringBetween(html, head, ">");
	String type = StringUtils.substringBetween(inside, "charset=", "\"");

	// if the HTML contains no data, fall back to auto-detection
	if (inside.length() == html.length() || type.length() == inside.length())
	    return null;
	else
	    return Charset.forName(type);
    }

    public static Charset[] toCharsetArray(String[] encodings) {
	Charset[] charsets = new Charset[encodings.length];
	for (int i = 0; i < encodings.length; i++)
	    charsets[i] = Charset.forName(encodings[i]);
	return charsets;
    }

    /**
         * Returns null if no encoding was found
         * 
         * @param nLanguageHint
         *                One of the values enumerated in this class.
         * @throws IOException
         */
    public static Charset getMostProbableEncoding(InputStream in, int nLanguageHint)
	    throws IOException {
	String[] encodings = getProbableEncodings(in, nLanguageHint);
	if (encodings.length > 0) {
	    return Charset.forName(encodings[0]);
	} else {
	    return null;
	}
    }

    /**
         * Returns null if no encoding was found
         * 
         * @param nLanguageHint
         *                One of the values enumerated in this class.
         * @throws IOException
         */
    public static Charset getMostProbableEncoding(byte[] bytes, int nLanguageHint)
	    throws IOException {
	String[] encodings = getProbableEncodings(bytes, nLanguageHint);
	if (encodings.length > 0) {
	    return Charset.forName(encodings[0]);
	} else {
	    return null;
	}
    }

    /**
         * @param nLanguageHint
         *                One of the values enumerated in this class.
         * @throws IOException
         */
    public static String[] getProbableEncodings(byte[] bytes, int nLanguageHint) throws IOException {

	nsDetector det = new nsDetector(nLanguageHint);

	// Check if the stream is only ascii.
	boolean isAscii = det.isAscii(bytes, bytes.length);

	// DoIt if non-ascii and not done yet.
	if (!isAscii)
	    det.DoIt(bytes, bytes.length, false);

	det.DataEnd();

	if (isAscii) {
	    return new String[] { "ASCII" };
	} else {
	    String[] encodings = det.getProbableCharsets();
	    if (encodings[0] == "nomatch")
		return new String[0];
	    else
		return encodings;
	}
    }

    /**
         * @param nLanguageHint
         *                One of the values enumerated in this class.
         * @throws IOException
         */
    public static String[] getProbableEncodings(InputStream in, int nLanguageHint)
	    throws IOException {

	nsDetector det = new nsDetector(nLanguageHint);

	byte[] buf = new byte[1024];
	int len;
	boolean done = false;
	boolean isAscii = true;

	final Mutable<String> charsetFound = new Mutable<String>();

	// Set an observer...
	// The Notify() will be called when a matching charset is found.

	det.Init(new nsICharsetDetectionObserver() {
	    public void Notify(String charset) {
		charsetFound.value = charset;
	    }
	});

	while ((len = in.read(buf, 0, buf.length)) != -1 && charsetFound.value == null) {

	    // Check if the stream is only ascii.
	    if (isAscii)
		isAscii = det.isAscii(buf, len);

	    // DoIt if non-ascii and not done yet.
	    if (!isAscii && !done)
		done = det.DoIt(buf, len, false);
	}

	if (charsetFound.value != null)
	    return new String[] { charsetFound.value };

	det.DataEnd();
	in.close();

	if (isAscii) {
	    return new String[] { "ASCII" };
	} else {
	    String[] encodings = det.getProbableCharsets();
	    if (encodings[0] == "nomatch") {
		return new String[0];
	    } else {
		return encodings;
	    }
	}
    }

    public static void main(String[] args) throws Exception {
	// InputStream in =
	// NetUtils.getUrlStream("http://search.itmedia.co.jp/");
	// FileInputStream in = new
	// FileInputStream("/media/disk/research/corpora/jpen/ja/download/00000/page1.txt");

	// String file = FileUtils.getFileAsString(new File(
	// "/media/disk/research/corpora/jpen/ja/download/00000/page1.txt"));
	// String[] prob = getProbableEncodings(file.getBytes("UTF-16"),
	// nsPSMDetector.JAPANESE);
	// for (final String s : prob)
	// System.out.println(s);

	System.out.println(getEncodingFromHtml(new File(
		"/media/disk/research/corpora/jpen/ja/move/01000/page1006")));
    }
}
