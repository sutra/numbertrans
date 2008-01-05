package info.jonclark.corpus.parallel;

import info.jonclark.corpus.management.documents.InputDocument;
import info.jonclark.corpus.management.documents.OutputDocument;
import info.jonclark.corpus.management.etc.CorpusManException;
import info.jonclark.corpus.management.iterators.interfaces.ParallelCorpusAlignmentIterator;
import info.jonclark.corpus.management.runs.ParallelCorpusAlignmentRun;
import info.jonclark.log.LogUtils;
import info.jonclark.properties.PropertyUtils;
import info.jonclark.stat.RemainingTimeEstimator;
import info.jonclark.util.FileUtils;
import info.jonclark.util.FormatUtils;
import info.jonclark.util.ProcessUtils;
import info.jonclark.util.ReflectUtils;
import info.jonclark.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class ChampollionAligner implements ParallelCorpusAlignmentRun {

    private final String pathToChampollion;
    private final String dictionaryFile;
    private final String stopListFile;
    private final double ratio;

    private static final Logger log = LogUtils.getLogger();

    public ChampollionAligner(Properties props, String runName, String corpusName) {
	this.pathToChampollion = props.getProperty("pathToChapollion");
	this.dictionaryFile = props.getProperty("dictionaryFile");
	this.stopListFile = props.getProperty("stopListFile");

	assert pathToChampollion != null;
	assert dictionaryFile != null;
	assert stopListFile != null;

	int englishCharCount = Integer.parseInt(props.getProperty("englishCharCount"));
	int foreignCharCount = Integer.parseInt(props.getProperty("foreignCharCount"));
	this.ratio = (double) foreignCharCount / (double) englishCharCount;
    }

    public void processCorpus(ParallelCorpusAlignmentIterator iterator) throws CorpusManException {
	try {
	    while (iterator.hasNext()) {
		iterator.next();

		InputDocument inE = iterator.getInputDocumentE();
		InputDocument inF = iterator.getInputDocumentF();
		OutputDocument outE = iterator.getOutputDocumentE();
		OutputDocument outF = iterator.getOutputDocumentF();
		OutputDocument outA = iterator.getAlignedOutputDocument();

		File alignments = File.createTempFile("alignments", ".txt");

		final int MAX_LINES = 100;
		if (StringUtils.countOccurances(inE.getWholeFile(), '\n') > MAX_LINES
			|| StringUtils.countOccurances(inF.getWholeFile(), '\n') > MAX_LINES) {
		    // skip articles with more than 100 lines; they're probably garbage
		    ;
		} else {
		    // execute champollion
		    alignSentences(inE.getFile(), inF.getFile(), alignments);

		    ChampollionAlignmentReader.createAlignedFile(inE.getFile(), inF.getFile(),
			    alignments, outE, outF, outA);
		}
		
		alignments.delete();

		inE.close();
		inF.close();
		outE.close();
		outF.close();
		outA.close();
	    }
	    iterator.finish();
	} catch (IOException e) {
	    throw new CorpusManException(e);
	} catch (InterruptedException e) {
	    throw new CorpusManException(e);
	}
    }

    public void alignSentences(File enFile, File foreignFile, File alignmentOutputFile)
	    throws InterruptedException, IOException {
	String commandLine = pathToChampollion + " -d " + dictionaryFile + " -s " + stopListFile
		+ " -c " + FormatUtils.formatDouble4(ratio) + " " + enFile.getAbsolutePath() + " "
		+ foreignFile.getAbsolutePath() + " " + alignmentOutputFile.getAbsolutePath();

	log.fine("Running: " + commandLine);
	ProcessUtils.runProcessWait(commandLine, false);
    }

    public static void main(String... args) throws Exception {
	if (args.length != 4) {
	    System.err.println("Usage: "
		    + ReflectUtils.getCurrentClassName()
		    + " <properties_file> <sentencesFilesEn> <sentencesExtForeign> <sentenceDirForeign> <alignmentsFileExt>");
	    System.exit(1);
	}

	Properties props = PropertyUtils.getProperties(args[0]);
	ChampollionAligner aligner = new ChampollionAligner(props, null, null);

	System.out.println("Finding sentences files for X...");
	File[] filesX = FileUtils.getFilesFromWildcard(args[1]);
	System.out.println(filesX.length + " files found.");

	String sentExtForeign = args[2];
	// String sentDirForeign = args[3];
	String alignmentsFileExt = args[4];

	RemainingTimeEstimator time = new RemainingTimeEstimator(500);

	for (int i = 0; i < filesX.length; i++) {
	    File alignmentFile = FileUtils.changeFileExt(filesX[i], alignmentsFileExt);

	    File fileY = FileUtils.changeFileExt(filesX[i], sentExtForeign);
	    // fileY = FileUtils.changeFileDir(fileY, sentDirForeign);
	    //			 
	    //			
	    // File fileY = new File(filesX[i].getParentFile(), name);
	    // File alignmentFile = new File(filesX[i].getParentFile(),
	    // alignmentFileName);
	    aligner.alignSentences(filesX[i], fileY, alignmentFile);

	    time.recordEvent();
	    System.out.println("Estimated completion at: "
		    + time.getEstimatedCompetionTimeFormatted(filesX.length - i));
	}

	System.out.println("Done.");
    }
}
