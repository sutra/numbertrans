package info.jonclark.corpus.parallel;

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

public class ChampollionAligner {

	private final String pathToChampollion;
	private final String dictionaryFile;
	private final String stopListFile;
	private final double ratio;

	public ChampollionAligner(Properties props) {
		this.pathToChampollion = props.getProperty("pathToChapollion");
		this.dictionaryFile = props.getProperty("ditionaryFile");
		this.stopListFile = props.getProperty("stopListFile");

		int englishCharCount = Integer.parseInt(props.getProperty("englishCharCount"));
		int foreignCharCount = Integer.parseInt(props.getProperty("foreignCharCount"));
		this.ratio = (double) foreignCharCount / (double) englishCharCount;
	}

	public void alignSentences(File enFile, File foreignFile, File alignmentOutputFile)
			throws InterruptedException, IOException {
		String commandLine = pathToChampollion + " -d " + dictionaryFile + " -s " + stopListFile
				+ " -c " + FormatUtils.formatDouble4(ratio) + " " + enFile.getAbsolutePath() + " "
				+ foreignFile.getAbsolutePath() + " " + alignmentOutputFile.getAbsolutePath();

		System.out.println("Running: " + commandLine);
		ProcessUtils.runProcessWait(commandLine, true);
	}

	public static void main(String... args) throws Exception {
		if (args.length != 4) {
			System.err.println("Usage: " + ReflectUtils.getCurrentClassName()
					+ " <properties_file> <sentencesFilesEn> <sentencesExtForeign> <sentenceDirForeign> <alignmentsFileExt>");
			System.exit(1);
		}
		
		Properties props = PropertyUtils.getProperties(args[0]);
		ChampollionAligner aligner = new ChampollionAligner(props);

		System.out.println("Finding sentences files for X...");
		File[] filesX = FileUtils.getFilesFromWildcard(args[1]);
		System.out.println(filesX.length + " files found.");

		String sentExtForeign = args[2];
		String sentDirForeign = args[3];
		String alignmentsFileExt = args[4];
		
		RemainingTimeEstimator time = new RemainingTimeEstimator(500);

		for (int i = 0; i < filesX.length; i++) {
			File alignmentFile = FileUtils.changeFileExt(filesX[i], alignmentsFileExt);
			
			File fileY = FileUtils.changeFileExt(filesX[i], sentExtForeign);
//			fileY = FileUtils.changeFileDir(fileY, sentDirForeign);
//			 
//			
//			File fileY = new File(filesX[i].getParentFile(), name);
//			File alignmentFile = new File(filesX[i].getParentFile(), alignmentFileName);
			aligner.alignSentences(filesX[i], fileY, alignmentFile);
			
			time.recordEvent();
			System.out.println("Estimated completion at: " + time.getEstimatedCompetionTimeFormatted(filesX.length - i));
		}

		System.out.println("Done.");
	}
}
