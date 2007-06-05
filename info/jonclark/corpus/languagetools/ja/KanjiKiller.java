package info.jonclark.corpus.languagetools.ja;

import info.jonclark.log.LogUtils;
import info.jonclark.util.HtmlUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Attempts to transform kanji into their phonetic pronunciation using JMDict.
 * 
 */
public class KanjiKiller {

	private final HashMap<String, ArrayList<String>> kanjiToKana = new HashMap<String, ArrayList<String>>(
			150000);
	private final int maxKanjiChunkLength = 5;

	private int nAmbiguous = 0;
	private int nGiveUp = 0;
	private int[] hitsAtLength;

	private final boolean separateResults;

	private static final Logger log = LogUtils.getLogger();

	public KanjiKiller(String jmDictXmlFile, boolean separateResults) throws IOException {
		loadDict(jmDictXmlFile);
		this.hitsAtLength = new int[maxKanjiChunkLength];
		this.separateResults = separateResults;
	}

	private void loadDict(String file) throws IOException {
		boolean inEntry = false;
		int count = 0;
		double length = 0;
		int maxLength = 0;

		log.info("Loading kanji to hiragana dictionary...");

		BufferedReader in = new BufferedReader(new FileReader(file));

		final int DEFAULT_SIZE = 4;
		final ArrayList<String> newKanji = new ArrayList<String>(DEFAULT_SIZE);
		ArrayList<String> newHiragana = new ArrayList<String>(DEFAULT_SIZE);

		String line = in.readLine();
		while (line != null) {
			if (line.equals("</entry>")) {
				inEntry = false;

				for (final String k : newKanji) {
					ArrayList<String> existingHiragana = kanjiToKana.get(k);
					if (existingHiragana == null)
						existingHiragana = new ArrayList<String>(DEFAULT_SIZE);
					existingHiragana.addAll(newHiragana);

					kanjiToKana.put(k, existingHiragana);
					count++;
				}

				newHiragana.clear();
				newKanji.clear();
			}

			if (inEntry) {
				if (line.startsWith("<keb>")) {
					String k = HtmlUtils.extractNonNestedTag(line, "keb");
					newKanji.add(k);

					maxLength = Math.max(maxLength, k.length());
					length += k.length();
				} else if (line.startsWith("<reb>")) {
					String kana = HtmlUtils.extractNonNestedTag(line, "reb");
					newHiragana.add(kana);
				}
			}

			if (line.equals("<entry>"))
				inEntry = true;

			line = in.readLine();
		}

		log.info(count + " kanji words in dictionary" + "\nMax length: " + maxLength
				+ "\nAvg length: " + length / (double) count);
	}

	public String killKanji(final String kanjiChunk) {
		assert kanjiChunk.length() < 10 : "Could any kanji chunk really be this big: " + kanjiChunk;

		StringBuilder builder = new StringBuilder();

		// try different possibilities for this chunk
		// ouch! my runtime!
		int nBegin = 0;
		while (nBegin < kanjiChunk.length()) {

			// first try the whole thing, then start backing off
			boolean matched = false;
			int nEnd = Math.min(kanjiChunk.length(), maxKanjiChunkLength);
			while (nEnd > nBegin && !matched) {
				String query = kanjiChunk.substring(nBegin, nEnd);
				ArrayList<String> kanaResults;
				if ((kanaResults = kanjiToKana.get(query)) != null) {
					matched = true;

					if (kanaResults.size() > 1)
						nAmbiguous++;

					if (separateResults)
						builder.append(kanaResults.get(0) + " ");
					else
						builder.append(kanaResults.get(0));
				} else {
					// backoff
					nEnd--;
				}
			}

			if (nBegin == nEnd) {
				// give up on this kanji
				builder.append(kanjiChunk.substring(nBegin, nBegin + 1) + "");
				nBegin++;
				nGiveUp++;
			} else {
				// we got a hit
				hitsAtLength[nEnd - nBegin]++;
				nBegin = nEnd;
			}
		}

		return builder.toString();
	}

	public void printStats() {
		System.out.println("Number of times gave up: " + nGiveUp);
		for (int i = 0; i < hitsAtLength.length; i++) {
			System.out.println("Number of hits of length " + i + ": " + hitsAtLength[i]);
		}
		System.out.println("Number of ambiguous results: " + nAmbiguous);
	}
}
