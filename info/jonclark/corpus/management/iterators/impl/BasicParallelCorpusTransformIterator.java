package info.jonclark.corpus.management.iterators.impl;

import java.io.File;
import java.util.Properties;

import info.jonclark.corpus.management.directories.AbstractCorpusDirectory;
import info.jonclark.corpus.management.directories.RootCorpusDirectory;
import info.jonclark.corpus.management.etc.CorpusProperties;
import info.jonclark.corpus.management.iterators.ParallelCorpusTransformIterator;
import info.jonclark.properties.PropertiesException;

public class BasicParallelCorpusTransformIterator implements ParallelCorpusTransformIterator {

	private final File[] inputDocumentsA;
	private final File[] inputDocumentsB;
	private final File[] outputDocumentsA;
	private final File[] outputDocumentsB;
	private final AbstractCorpusDirectory root;
	private int nCurrentDocument = -1;

	public BasicParallelCorpusTransformIterator(Properties props, String corpusName, String runName)
			throws PropertiesException {
		
		this.root = new RootCorpusDirectory(props, corpusName);

		String runNamespace = CorpusProperties.getRunNamespace(props, corpusName, runName);
		String inputRun = CorpusProperties.getInputRunName(props, runNamespace);
		
		inputDocumentsA = root.getDocuments(0, inputRun);
		inputDocumentsB = root.getDocuments(1, inputRun);
		outputDocumentsA = root.getDocuments(0, runName);
		outputDocumentsB = root.getDocuments(1, runName);
	}

	public File getInputDocumentA() {
		return inputDocumentsA[nCurrentDocument];
	}

	public File getInputDocumentB() {
		return inputDocumentsB[nCurrentDocument];
	}

	public File getOutputDocumentA() {
		return outputDocumentsA[nCurrentDocument];
	}

	public File getOutputDocumentB() {
		return outputDocumentsB[nCurrentDocument];
	}

	public boolean hasNextPair() {
		return nCurrentDocument < inputDocumentsA.length - 1;
	}

	/**
	 * Must be called AFTER each pair of documents is accessed to get to the
	 * next pair.
	 */
	public void nextPair() {
		nCurrentDocument++;
	}

}
