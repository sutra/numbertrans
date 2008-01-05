/*
 * Created on Jun 25, 2007
 */
package info.jonclark.corpus.languagetools.ja;

import info.jonclark.corpus.EncodingDetector;
import info.jonclark.corpus.management.documents.MetaDocument;
import info.jonclark.corpus.management.documents.OutputDocument;
import info.jonclark.corpus.management.etc.BadFilenameException;
import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.iterators.interfaces.ParallelCorpusCreationIterator;
import info.jonclark.corpus.management.runs.ParallelCorpusCreationRun;
import info.jonclark.log.LogUtils;
import info.jonclark.util.FileUtils;
import info.jonclark.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Move JPEN to the new corpus management system
 */
public class MoveJPEN implements ParallelCorpusCreationRun {

    private static final Logger log = LogUtils.getLogger();

    public MoveJPEN(Properties props, String runName, String corpusName) {

    }

    public void processCorpus(ParallelCorpusCreationIterator iterator) throws CorpusManException {

	try {
	    String jaFilter = "/media/disk/research/corpora/jpen.old/jp/*/*.html";
	    log.info("Finding files for pattern: " + jaFilter);
	    File[] jaFiles = FileUtils.getFilesFromWildcard(jaFilter);

	    String enFilter = "/media/disk/research/corpora/jpen.old/en/*/*.html";
	    log.info("Finding files for pattern: " + enFilter);
	    File[] enFiles = FileUtils.getFilesFromWildcard(enFilter);
	    HashSet<String> en = new HashSet<String>();
	    for (final File f : enFiles)
		en.add(StringUtils.substringBefore(f.getName(), "."));

	    iterator.setExpectedDocumentCount(jaFiles.length);
	    Charset utf8 = Charset.forName("utf8");
	    int nDiscarded = 0;
	    for (final File fileJA : jaFiles) {
		iterator.next();

		String name = StringUtils.substringBefore(fileJA.getName(), ".");
		if (iterator.shouldSkip(name)) {
		    System.out.print("o");
		} else {

		    String pathEN = StringUtils.replaceFast(fileJA.getAbsolutePath(), "/jp/",
			    "/en/");
		    pathEN = StringUtils.replaceFast(pathEN, ".utf8.txt", ".orig.txt");
		    File fileEN = new File(pathEN);

		    if (en.contains(name)) {

			OutputDocument outE = iterator.getOutputDocumentE(name);
			OutputDocument outF = iterator.getOutputDocumentF(name);

			Charset jaEncoding = EncodingDetector.getEncodingFromHtml(fileJA);

			if (jaEncoding != null) {
			    outE.println(FileUtils.getFileAsString(fileEN, utf8));
			    outF.println(FileUtils.getFileAsString(fileJA, jaEncoding));
			    outE.close();
			    outF.close();
			} else {
			    nDiscarded++;
			}
		    }
		}
	    }

	    log.info("Discarded " + nDiscarded + " due to undetected encoding.");
	    MetaDocument meta = iterator.getRunMetaData();
	    meta.set("discarded", nDiscarded + "");
	    meta.close();

	    iterator.finish();
	} catch (IOException e) {
	    throw new CorpusManException(e);
	} catch (BadFilenameException e) {
	    throw new CorpusManException(e);
	}
    }
}
