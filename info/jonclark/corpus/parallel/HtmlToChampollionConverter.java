/*
 * Created on May 26, 2007
 */
package info.jonclark.corpus.parallel;

import java.io.File;
import java.util.ArrayList;

import info.jonclark.stat.RemainingTimeEstimator;
import info.jonclark.util.FileUtils;
import info.jonclark.util.HtmlUtils;
import info.jonclark.util.StringUtils;

/**
 * Converts a page in HTML format into the format expected by the Champollion
 * aligner.
 */
public class HtmlToChampollionConverter {

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

    public static void main(String... args) throws Exception {

	if (args.length != 1) {
	    System.err.println("Usage: program <dir1:dir2:dir3...>");
	    System.exit(1);
	}

	final RemainingTimeEstimator est = new RemainingTimeEstimator(500);
	final ArrayList<File> htmlFiles = new ArrayList<File>(40000);

	String[] dirs = StringUtils.tokenize(args[0], ",");
	for (final String dir : dirs) {
	    for (File htmlFile : FileUtils.getFilesWithExt(new File(dir), ".html")) {
		htmlFiles.add(htmlFile);
	    }
	}

	for (int i = 0; i < htmlFiles.size(); i++) {
	    final File htmlFile = htmlFiles.get(i);

	    // Step 1: Get the HTML
	    String html = FileUtils.getFileAsString(htmlFile);

	    // Step 2: Extract just the body
	    html = filterNonContent(html);

	    // Step 3: Try to use HTML structure to produce better sentences
	    // We do not replace with the exact tags since they will be
	    // removed anyway
	    // We replace with versions of the same length
	    final String[] oldArr = new String[] { "<br>", "</h1>", "</h2>", "</h3>", "</h4>",
		    "</h5>" };
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

	    // Step 6: save file
	    final String fileName = StringUtils.substringBefore(htmlFile.getName(), ".html")
		    + ".txt";
	    FileUtils.saveFileFromString(new File(htmlFile.getParentFile(), fileName), plainText);

	    est.recordEvent();
	    if (i % 100 == 0) {
		System.out.println("Processing file " + htmlFile.getName()
			+ "; estimated completion at "
			+ est.getEstimatedCompetionTimeFormatted(htmlFiles.size() - i));
	    }
	}
    }
}
