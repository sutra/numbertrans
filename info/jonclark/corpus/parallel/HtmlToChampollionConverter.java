/*
 * Created on May 26, 2007
 */
package info.jonclark.corpus.parallel;

import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.iterators.interfaces.UniCorpusTransformIterator;
import info.jonclark.corpus.management.runs.UniCorpusTransformRun;
import info.jonclark.corpus.management.documents.InputDocument;
import info.jonclark.corpus.management.documents.OutputDocument;
import info.jonclark.stat.RemainingTimeEstimator;
import info.jonclark.util.FileUtils;
import info.jonclark.util.HtmlUtils;
import info.jonclark.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Converts a page in HTML format into the format expected by the Champollion
 * aligner.
 */
public class HtmlToChampollionConverter implements UniCorpusTransformRun {

    public HtmlToChampollionConverter(Properties props, String runName, String corpusName) {

    }

    private static String filterNonContent(String html) {
	// include some hacks for specific sites to get just the article
	// text
	html = HtmlUtils.extractBody(html);
	// wired EN
	html = HtmlUtils.extractFirstOf(html, "<div id=\"article_text\"", "</div>", true);
	// hotwired JP
	html = HtmlUtils.extractFirstOf(html, "<!-- ARTICLE/-->", "<!-- /ARTICLE-->", true);
	// no optimization for www.internetnews.com
	// no optimization for japan.internet.com
	// dc.internet.com & www.wi-fiplanet.com &
	// itmanagement.earthweb.com & www.searchenginewatch.com
	html = HtmlUtils.extractFirstOf(html, "<!--content_start-->", "<!--content_stop-->", true);
	// www.linuxplanet.com
	html = HtmlUtils.extractFirstOf(html, "<!--**** begin content ****-->",
		"<!--**** end content ****-->", true);
	// no optimization for www.phpbuilder.com
	// www.winplanet.com
	html = HtmlUtils.extractFirstOf(html, "<!--start_reviews -->", "<!--end_reviews-->", true);
	// no optimization for www.clickz.com
	// www.eweek.com
	html = HtmlUtils.extractFirstOf(html, "<td align=\"left\" class=\"Article_Content\"",
		"</td>", true);
	// news.com.com
	html = HtmlUtils.extractFirstOf(html, "<div class=\"rb_content\">",
		"<div id=\"storyFoot\">", true);
	// business.newsforge.com
	html = HtmlUtils.extractFirstOf(html, "<div class=\"article_box\">",
		"<div class=\"userboxes\">", true);
	// japan.cnet.com
	html = HtmlUtils.extractFirstOf(html, "<div class=\"leaf_body\">",
		"<div class=\"leaf_body_foot\">", true);
	// news.zdnet.com
	html = HtmlUtils.extractFirstOf(html, "<ul class=\"topics\">",
		"<ul class=\"storyOptions\">", true);

	return html;
    }

    public static String toPlainText(String html) {

	// Step 2: Extract just the body
	html = filterNonContent(html);

	// Step 3: Try to use HTML structure to produce better sentences
	// We do not replace with the exact tags since they will be
	// removed anyway
	// We replace with versions of the same length
	final String[] oldArr = new String[] { "<br>", "</h1>", "</h2>", "</h3>", "</h4>", "</h5>" };
	final String[] replacementArr = new String[] { "<r>\n", "<h1>\n", "<h2>\n", "<h3>\n",
		"<h4>\n", "<h5>\n" };
	html = StringUtils.replaceFast(html, oldArr, replacementArr);

	// Step 4: remove all JavaScript and HTML tags
	html = HtmlUtils.removeScript(html);
	html = HtmlUtils.removeAllTags(html);

	// Step 5: Cleanup
	// Unescape any HTML character codes
	// Remove space from beginnings and ends of lines
	// Remove blank lines
	html = HtmlUtils.unescape(html);
	final String[] lines = StringUtils.tokenize(html, "\r\n");
	final StringBuilder builder = new StringBuilder(html.length());
	for (int j = 0; j < lines.length; j++) {
	    lines[j] = lines[j].trim();
	    if (!lines[j].equals("")) {
		builder.append(lines[j] + "\n");
	    }
	}

	String plainText = builder.toString();
	return plainText;
    }

    public void processCorpus(UniCorpusTransformIterator iterator) throws CorpusManException {
	try {
	    while (iterator.hasNext()) {

		iterator.next();

		InputDocument in = iterator.getInputDocument();
		OutputDocument out = iterator.getOutputDocument();

		String html = in.getWholeFile();
		String plainText = toPlainText(html);
		out.println(plainText);

		in.close();
		out.close();

	    }
	    iterator.finish();
	} catch (IOException e) {
	    throw new CorpusManException(e);
	}
    }

    public static void main(String... args) throws Exception {

	if (args.length != 1) {
	    System.err.println("Usage: program <file_path_with_wildcards>");
	    System.exit(1);
	}

	if (!args[0].endsWith("*.html")) {
	    System.err.println("Wildcard filter should end with '*.html'");
	    System.exit(1);
	}

	final RemainingTimeEstimator est = new RemainingTimeEstimator(500);
	final File[] htmlFiles = FileUtils.getFilesFromWildcard(args[0]);

	for (int i = 0; i < htmlFiles.length; i++) {
	    final File htmlFile = htmlFiles[i];

	    // Step 1: Get the HTML
	    String html = FileUtils.getFileAsString(htmlFile);
	    String plainText = toPlainText(html);

	    // Step 6: save file
	    final String fileName = StringUtils.substringBefore(htmlFile.getName(), ".html")
		    + ".txt";
	    FileUtils.saveFileFromString(new File(htmlFile.getParentFile(), fileName), plainText);

	    est.recordEvent();
	    if (i % 100 == 0) {
		System.out.println("Processing file " + htmlFile.getName()
			+ "; estimated completion at "
			+ est.getEstimatedCompetionTimeFormatted(htmlFiles.length - i));
	    }
	}
    }
}
