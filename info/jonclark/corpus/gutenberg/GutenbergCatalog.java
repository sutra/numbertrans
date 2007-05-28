/*
 * Copyright (c) 2006, Jonathan Clark <jon_DOT_h_DOT_clark_AT_gmail_DOT_com> 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of my affiliates nor the names of thier contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIEDWARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package info.jonclark.corpus.gutenberg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Logger;

import info.jonclark.corpus.CorpusAuthor;
import info.jonclark.corpus.CorpusDocument;
import info.jonclark.corpus.UniqueWordCounter;
import info.jonclark.corpus.interfaces.CorpusStatistics;
import info.jonclark.log.LogUtils;
import info.jonclark.log.VarLogger;
import info.jonclark.properties.PropertiesException;
import info.jonclark.properties.PropertyUtils;
import info.jonclark.stat.RemainingTimeEstimator;
import info.jonclark.stat.SecondTimer;
import info.jonclark.util.ArrayUtils;
import info.jonclark.util.BufferedZipInputStream;
import info.jonclark.util.DebugUtils;
import info.jonclark.util.NetUtils;
import info.jonclark.util.StringUtils;

/**
 * @author Jonathan
 */
public class GutenbergCatalog implements Serializable, CorpusStatistics {

    private static final boolean useIntern = false;
    private static final long serialVersionUID = -2515675662692379929L;
    public static transient final String CATALOG_FEED_RDF_ZIP = "http://www.gutenberg.org/feeds/catalog.rdf.zip";

    private transient static final VarLogger log = LogUtils.getLogger();
    private transient final Properties props;

    private final TreeMap<String, GutenbergDocument> mapDocuments = new TreeMap<String, GutenbergDocument>();
    private final TreeMap<String, CorpusAuthor> mapAuthors = new TreeMap<String, CorpusAuthor>(
	    String.CASE_INSENSITIVE_ORDER);

    private static transient final int STATUS_EVERY_X_LINES = 25000;
    private static transient final int SAVE_EVERY_X_ETEXTS = 10;
    private static transient final String REQUIRED_ETEXT_FORMAT = ".zip";
    private static transient final String RDF_FORMAT_TEXT_PLAIN = "<dc:format><dcterms:IMT><rdf:value>text/plain;";

    private final boolean requireCreator = true;
    private final boolean requireTitle = true;
    private final boolean requireLanguage = true;

    private final UniqueWordCounter globalWordCount = new UniqueWordCounter(useIntern, false);
    private long nGlobalSentenceCount = 0;

    private static transient final String strEtextOpen = "<pgterms:etext rdf:ID=\"etext";
    private static transient final String strEtextClose = "</pgterms:etext>";
    private static transient final String strCreatorOpen = "<dc:creator";
    private static transient final String strCreatorClose = "</dc:creator>";
    private static transient final String strNestedOpen = "<rdf:li";
    private static transient final String strNestedClose = "</rdf:li>";
    private static transient final String strTitleOpen = "<dc:title";
    private static transient final String strTitleClose = "</dc:title>";
    private static transient final String strFriendlyTitleOpen = "<pgterms:friendlytitle";
    private static transient final String strFriendlyTitleClose = "</pgterms:friendlytitle>";
    private static transient final String strLanguageOpen = "<dc:language><dcterms:ISO639-2><rdf:value";
    private static transient final String strLanguageClose = "</rdf:value></dcterms:ISO639-2></dc:language>";

    private static transient final String strFileOpen = "<pgterms:file rdf:about=\"&f;";
    private static transient final String strFileClose = "</pgterms:file>";
    private static transient final String strResource = "<dcterms:isFormatOf rdf:resource=\"#etext";

    public GutenbergCatalog(Properties props) {
	assert props != null : "props parameter cannot be null";
	this.props = props;
    }

    public void updateCatalogFromWeb() throws IOException, GutenbergCatalogException,
	    PropertiesException {
	// this is an excellent canidate a counter, if we know how many etexts
	// we expect
	// do not use the XML parser b/c it will be too slow

	// TODO: Check for required properties at program startup.
	// Move all checking there

	InputStream stream = NetUtils.getUrlStream(CATALOG_FEED_RDF_ZIP);
	BufferedZipInputStream in = new BufferedZipInputStream(stream);
	if (in.nextEntry()) { // only read first entry

	    String line = null;

	    boolean inEtext = false;
	    boolean inFileTag = false;
	    boolean isPlainText = false;

	    // attributes of this text -- these will be their own class file
	    GutenbergDocument doc = null;
	    String relativePath = null;
	    int nTexts = 0; // number of etext entires read so far
	    int nIgnoredTexts = 0;
	    int nFiles = 0;
	    int nIgnoredFiles = 0;
	    int nLines = 0;

	    // NOTE: Tags can span multiple lines
	    // Each tag may read in the next line of its own accord
	    while ((line = in.readLine()) != null) {

		// System.out.println(line);

		int nIndex = -1;
		String strResult = null;
		if ((nIndex = line.indexOf(strEtextOpen)) > -1) {
		    final int nBegin = nIndex + strEtextOpen.length();
		    final int nEnd = line.indexOf("\"", nBegin);
		    doc = new GutenbergDocument(props);
		    String id = line.substring(nBegin, nEnd);
		    doc.setId(id);
		    inEtext = true;
		} else if ((nIndex = line.indexOf(strFileOpen)) > -1) {
		    final int nBegin = nIndex + strFileOpen.length();
		    final int nEnd = line.indexOf("\"", nBegin);
		    relativePath = "dirs/" + line.substring(nBegin, nEnd);
		    inFileTag = true;
		}

		if (inEtext) {
		    if ((nIndex = line.indexOf(strEtextClose)) > -1) {
			final String id = doc.getId();
			final String author = doc.getAuthor();
			final String title = doc.getTitle();
			final String language = doc.getLanguage();

			// do some error checking
			if (id.equals("")) {
			    log.finer("Ignoring an eText due to blank id");
			    nIgnoredTexts++;
			} else if (requireCreator && author.equals("")) {
			    log.finer("Ignoring an eText {0} due to blank creator", id);
			    nIgnoredTexts++;
			} else if (requireTitle && title.equals("")) {
			    log.finer("Ignoring an eText {0} due to blank title", id);
			    nIgnoredTexts++;
			} else if (requireLanguage && language.equals("")) {
			    log.finer("Ignoring an eText {0} due to blank language", id);
			    nIgnoredTexts++;
			} else {
			    addEntryToCatalog(doc);
			    inEtext = false;
			    nTexts++;
			}
		    } else if ((strResult = extractTag(in, line, strCreatorOpen, strCreatorClose)) != null) {
			doc.setAuthor(strResult);
		    } else if ((strResult = extractTag(in, line, strTitleOpen, strTitleClose)) != null) {
			doc.setTitle(strResult);
		    } else if ((strResult = extractTag(in, line, strFriendlyTitleOpen,
			    strFriendlyTitleClose)) != null) {
			doc.setFriendlyTitle(strResult);
		    } else if ((strResult = extractTag(in, line, strLanguageOpen, strLanguageClose)) != null) {
			doc.setLanguage(strResult);
		    }
		} // end if inEtext

		if (inFileTag) {
		    if ((nIndex = line.indexOf(strFileClose)) > -1) {
			inFileTag = false;
			isPlainText = false;
			relativePath = null;
			doc = null;
		    } else if ((nIndex = line.indexOf(RDF_FORMAT_TEXT_PLAIN)) > -1) {
			isPlainText = true;
		    } else if ((nIndex = line.indexOf(strResource)) > -1) {

			final int nBegin = nIndex + strResource.length();
			final int nEnd = line.indexOf("\"", nBegin);
			String id = line.substring(nBegin, nEnd);
			doc = mapDocuments.get(id);

			// there are many files per eText
			// we want the zipped plain text
			int nEtext = Integer.parseInt(id);
			if (nEtext < 10000 && isPlainText) {

			    if (doc != null) {
				doc.setRelativePath(relativePath);
				nFiles++;
			    } else {
				nIgnoredFiles++;
			    }

			} else {
			    nIgnoredFiles++;
			}

		    }
		} // end if inFileTag

		nLines++;
		if (nLines % GutenbergCatalog.STATUS_EVERY_X_LINES == 0)
		    log.info(nTexts + " eTexts read so far (" + nIgnoredTexts
			    + " etexts ignored due to errors) [" + nFiles + " files ("
			    + nIgnoredFiles + " files ignored due to errors] [" + nLines
			    + " lines]");

	    } // end while readLine

	    log.info("Sucessfully read " + nTexts + " eTexts (" + nIgnoredTexts
		    + " ignored due to errors)");

	} else { // no next entry
	    log.severe("Empty ZIP file.");
	}
    }

    /**
         * Add a document to the catalog while performing an update
         * 
         * @param doc
         */
    private void addEntryToCatalog(GutenbergDocument doc) {
	// write attributes of this etext to a database here
	/*
         * System.out.println("Etext: " + doc.getId());
         * System.out.println("Author: " + doc.getAuthor());
         * System.out.println("Title: " + doc.getTitle());
         * System.out.println("Language: " + doc.getLanguage());
         */
	mapDocuments.put(doc.getId(), doc);

	CorpusAuthor author = mapAuthors.get(doc.getAuthor());
	if (author == null) {
	    author = new CorpusAuthor(doc.getAuthor());
	    mapAuthors.put(doc.getAuthor(), author);
	}
	author.addDocument(doc);
    }

    /**
         * For all documents currently in the document, calculate word counts
         * and other such statistics from a local Project Gutenberg repository.
         * 
         * @throws IOException
         * @throws IOException
         */
    public void analyzeCatalogedDocuments(String pathToGutenbergRepository) throws IOException {

	int nAlreadyDone = 0;
	int nSuccess = 0;
	int nFailed = 0;
	boolean saved = true;
	RemainingTimeEstimator timer = new RemainingTimeEstimator(100);
	final Collection<GutenbergDocument> documents = mapDocuments.values();
	for (final GutenbergDocument doc : documents) {

	    // if there are zero words in a document, either we haven't
	    // analyzed it yet or there is a problem with the document. if
	    // the latter is the case, then it's very cheap to analyze this
	    // corrupt document
	    if (doc.getWordcount() == 0) {
		try {
		    doc.analyzeDocument(pathToGutenbergRepository);
		    globalWordCount.addCounter(doc.getUniqueWordCounter());
		    nGlobalSentenceCount += doc.getSentenceCount();
		    nSuccess++;
		    timer.recordEvent();
		    saved = false;
		} catch (IOException e) {
		    // log.warning("IOException for document {0}; Error:
		    // {1}", doc.getId(), e
		    // .getMessage());
		    nFailed++;
		}
	    } else {
		nGlobalSentenceCount += doc.getSentenceCount();
		globalWordCount.addCounter(doc.getUniqueWordCounter());
		doc.freezeCounts();
		nAlreadyDone++;
	    }

	    if (nSuccess % SAVE_EVERY_X_ETEXTS == 0 && !saved) {
		int nEventsRemaining = documents.size() - nSuccess - nFailed - nAlreadyDone;
		log.info(
			"Saving data after sucessfully analyzing {0} eTexts; {1} already done; {2} failed;"
				+ "{3} words; {4} unique words; {5} eTexts/second; remaining time: {6};estimated completion: {7}",
			nSuccess, nAlreadyDone, nFailed, globalWordCount.getNonuniqueWordCount(),
			globalWordCount.getUniqueWordCount(), timer.getEventsPerSecond(),
			timer.getRemainingTime(nEventsRemaining).toStringSingleUnit(),
			timer.getEstimatedCompetionTimeFormatted(nEventsRemaining));
		saveDocumentsToDisk();
		saved = true;
	    }
	}

	for (final CorpusAuthor author : mapAuthors.values()) {
	    author.updateStatistics();
	}
	saveAuthorsToDisk();
    }

    /**
         * Check if a tag starts on the current line. If it does, read the tag
         * to its completion and return its contents.
         * 
         * @param in
         * @param line
         * @param doc
         * @return The inner contents of the tag. null if no tag begins on this
         *         line
         * @throws IOException
         * @throws GutenbergCatalogException
         */
    private String extractTag(final BufferedZipInputStream in, String line,
	    final String strOpenTag, final String strCloseTag) throws IOException,
	    GutenbergCatalogException {
	final int nIndex;
	if ((nIndex = line.indexOf(strOpenTag)) > -1) {
	    StringBuffer buf = new StringBuffer();
	    final int nBegin = line.indexOf('>', nIndex + strOpenTag.length()) + 1;
	    String strReturn;

	    int nEnd = -1;
	    if ((nEnd = line.indexOf(strCloseTag, nBegin)) >= 0) {
		// the closing tag is on the same line as the opening tag
		strReturn = line.substring(nBegin, nEnd);
	    } else {
		// this tag spans multiple lines
		// append the first line, starting after the tag
		buf.append(line.substring(nBegin));
		while ((line = in.readLine()) != null && (nEnd = line.indexOf(strCloseTag)) < 0) {
		    buf.append(" " + line);
		}
		if (line == null) {
		    throw new GutenbergCatalogException("End of stream while reading inside tag."
			    + "Current buffer: " + buf.toString());
		}
		buf.append(" " + line.substring(0, nEnd));
		strReturn = buf.toString();
	    }

	    // check for nested tags in return string
	    String strResult = null;
	    if ((strResult = extractTag(in, strReturn, strNestedOpen, strNestedClose)) != null) {
		log.finer("Including only first author of multiple author work");
		strReturn = strResult;
	    }

	    return strReturn;
	} else {
	    return null;
	}
    }

    public float getMeanLengthOfSentence() {
	if (nGlobalSentenceCount > 0)
	    return globalWordCount.getNonuniqueWordCount() / nGlobalSentenceCount;
	else
	    return 0;
    }

    public long getSentenceCount() {
	return nGlobalSentenceCount;
    }

    public long getUniqueWordcount() {
	return globalWordCount.getUniqueWordCount();
    }

    public long getWordcount() {
	return globalWordCount.getNonuniqueWordCount();
    }

    public void saveDocumentsToDisk() throws IOException {
	String documentsFile = props.getProperty("documentsFile");
	PrintWriter out = new PrintWriter(documentsFile);

	out.println("ID|Author|Title|Language|Wordcount|Unique Wordcount|Sentences|MLS|Path");

	for (final GutenbergDocument doc : mapDocuments.values()) {
	    out.println(doc.getId() + "|" + doc.getAuthor() + "|" + doc.getTitle() + "|"
		    + doc.getLanguage() + "|" + doc.getWordcount() + "|" + doc.getUniqueWordcount()
		    + "|" + doc.getSentenceCount() + "|" + doc.getMeanLengthOfSentence() + "|"
		    + doc.getRelativePath());
	}

	out.flush();
	out.close();
    }

    public void loadDocumentsFromDisk() throws IOException, PropertiesException {
	String documentsFile = props.getProperty("documentsFile");
	BufferedReader in = new BufferedReader(new FileReader(documentsFile));

	String line = null;
	in.readLine(); // skip header row
	while ((line = in.readLine()) != null) {
	    String[] tokens = StringUtils.tokenize(line, "|");
	    GutenbergDocument doc = new GutenbergDocument(props);

	    doc.setId(tokens[0]);
	    doc.setAuthor(tokens[1]);
	    doc.setTitle(tokens[2]);
	    doc.setLanguage(tokens[3]);
	    doc.setCounts(Long.parseLong(tokens[4]), Long.parseLong(tokens[5]),
		    Long.parseLong(tokens[6]));
	    // skip tokens[7], the MLS
	    doc.setRelativePath(tokens[8]);

	    mapDocuments.put(doc.getId(), doc);
	}
    }

    public void saveAuthorsToDisk() throws IOException {
	String authorsFile = props.getProperty("authorsFile");
	PrintWriter out = new PrintWriter(authorsFile);

	out.println("Author|Wordcount|Unique Wordcount|Sentences|MLS|Documents");

	for (final CorpusAuthor author : mapAuthors.values()) {
	    out.println(author.getAuthorName() + "|" + author.getWordcount() + "|"
		    + author.getUniqueWordcount() + "|" + author.getSentenceCount() + "|"
		    + author.getMeanLengthOfSentence() + "|"
		    + formatDocumentList(author.getDocuments()));
	}

	out.flush();
	out.close();

	log.info("Wrote authors to file: {0}", authorsFile);
    }

    private String formatDocumentList(ArrayList<CorpusDocument> list) {
	StringBuilder builder = new StringBuilder();
	for (final CorpusDocument doc : list) {
	    builder.append(doc.getId() + ";");
	}
	return builder.toString();
    }

    public void loadAuthorsFromDisk() throws IOException, PropertiesException {
	String authorsFile = props.getProperty("authorsFile");
	BufferedReader in = new BufferedReader(new FileReader(authorsFile));

	String line = null;
	in.readLine(); // skip header row
	while ((line = in.readLine()) != null) {
	    String[] tokens = StringUtils.tokenize(line, "|");
	    CorpusAuthor author = new CorpusAuthor(tokens[0]);

	    author.setCounts(Long.parseLong(tokens[1]), Long.parseLong(tokens[2]),
		    Long.parseLong(tokens[3]));

	    // skip tokens[4], the MLS

	    String[] documentIds = StringUtils.tokenize(tokens[5], ";");
	    for (final String id : documentIds) {
		GutenbergDocument doc = mapDocuments.get(id);
		if (doc == null) {
		    log.warning("Required document not found: {0}", id);
		} else {
		    author.addDocument(doc);
		}
	    }

	    mapAuthors.put(author.getAuthorName(), author);
	}
    }

    public void clearAllStats() {
	for (final GutenbergDocument doc : mapDocuments.values()) {
	    doc.setCounts(0, 0, 0);
	}
	for (final CorpusAuthor author : mapAuthors.values()) {
	    author.setCounts(0, 0, 0);
	}
    }

    public static void main(String[] args) throws Exception {
	if (args.length < 1) {
	    System.err.println("Usage: program <properties_file> [options]");
	    System.err.println("--freshen-catalog\tFreshen the catalog from web data.");
	    System.err.println("--freshen-stats\tFreshen statistics by zeroing all"
		    + "statistics in the documents and authors file before beginning.");
	    System.exit(1);
	}

	// LogUtils.logToStdOut();
	LogUtils.logAll();
	DebugUtils.logAssertStatus(log);

	final Properties props = PropertyUtils.getProperties(args[0]);
	final String pathToGutenbergRepository = props.getProperty("pathToGutenbergRepository");

	GutenbergCatalog cat = new GutenbergCatalog(props);

	SecondTimer timer = new SecondTimer();
	timer.go();
	if (ArrayUtils.unsortedArrayContains(args, "--freshen-catalog")) {
	    log.info("Freshening catalog from web");
	    cat.updateCatalogFromWeb();
	    cat.saveDocumentsToDisk();
	    cat.saveAuthorsToDisk();
	} else {
	    cat.loadDocumentsFromDisk();
	    cat.loadAuthorsFromDisk();
	}
	timer.pause();
	log.info("Loaded catalog in {0} seconds", timer.getSecondsFormatted());

	if (ArrayUtils.unsortedArrayContains(args, "--freshen-stats")) {
	    log.info("Freshening (zeroing) statistics");
	    cat.clearAllStats();
	}

	cat.analyzeCatalogedDocuments(pathToGutenbergRepository);
    }
}
