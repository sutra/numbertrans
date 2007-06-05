/*
 * Created on May 26, 2007
 */
package info.jonclark.corpus.languagetools.ja;

import info.jonclark.util.HtmlUtils;
import info.jonclark.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Reads in an XML file in JMDict format and produces a dictionary in the format
 * that JMDict expects.
 */
public class JMDictChampollionFormatter {

	private final BufferedReader in;
	private final PrintWriter out;

	public JMDictChampollionFormatter(String inFile, String outFile) throws FileNotFoundException {
		in = new BufferedReader(new FileReader(inFile));
		out = new PrintWriter(outFile);
	}

	public void convert() throws IOException {
		boolean inEntry = false;
		int enCount = 0;

		final ArrayList<String> enWords = new ArrayList<String>(3);
		final ArrayList<String> foreignWords = new ArrayList<String>(3);

		String line = in.readLine();
		while (line != null) {
			if (line.equals("</entry>")) {
				inEntry = false;
				outputDictionaryEntry(enWords, foreignWords);
				enWords.clear();
				foreignWords.clear();
			}

			if (inEntry) {
				if (line.startsWith("<keb>")) {
					String kanji = HtmlUtils.extractNonNestedTag(line, "keb");
					foreignWords.add(kanji);
				} else if (line.startsWith("<reb>")) {
					String kana = HtmlUtils.extractNonNestedTag(line, "reb");
					foreignWords.add(kana);
				} else if (line.startsWith("<gloss>")) {
					String en = HtmlUtils.extractNonNestedTag(line, "gloss");
					en = removeFiller(en);
					enWords.add(en);
					enCount++;
				}
			}

			if (line.equals("<entry>"))
				inEntry = true;

			line = in.readLine();
		}

		System.out.println(enCount + " English words in dictionary");
	}

	private String removeFiller(String en) {
		en = StringUtils.substringAfter(en, "to ");
		en = StringUtils.substringAfter(en, "a ");
		en = StringUtils.substringAfter(en, "an ");
		en = StringUtils.substringAfter(en, "the ");
		en = HtmlUtils.removeTextBetween(en, "(", ")");
		return en;
	}

	private void outputDictionaryEntry(final Iterable<String> enWords,
			final Iterable<String> foreignWords) {
		for (final String enWord : enWords) {
			for (final String foreignWord : foreignWords) {
				if(!enWord.trim().equals("") && !foreignWord.trim().equals(""))
					out.println(enWord + " <> " + foreignWord);
			}
		}
	}

	public static void main(String... args) throws Exception {
		if (args.length != 2) {
			System.err.println("Usage: program <in_file> <out_file>");
			System.exit(1);
		}

		final String inFile = args[0];
		final String outFile = args[1];
		JMDictChampollionFormatter conv = new JMDictChampollionFormatter(inFile, outFile);
		conv.convert();
	}
}
