package info.jonclark.corpus.parallel;

import info.jonclark.corpus.management.documents.OutputDocument;
import info.jonclark.io.FileLineArray;
import info.jonclark.util.FileUtils;
import info.jonclark.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ChampollionAlignmentReader {

    public static void createAlignedFile(File sentencesFileX, File sentencesFileY,
	    File alignmentsFile, OutputDocument e, OutputDocument f, OutputDocument a)
	    throws IOException {

	FileLineArray sentencesX = new FileLineArray(sentencesFileX, FileLineArray.Mode.READ);
	FileLineArray sentencesY = new FileLineArray(sentencesFileY, FileLineArray.Mode.READ);

	BufferedReader in = new BufferedReader(new FileReader(alignmentsFile));
	String line;
	while ((line = in.readLine()) != null) {

	    if (!line.contains("omitted") && !line.equals("")) {

		try {
		    String[] alignment = StringUtils.split(line, " <=> ", 2);

		    int[] nLinesX = StringUtils.toIntArray(StringUtils.tokenize(alignment[0], ","));
		    int[] nLinesY = StringUtils.toIntArray(StringUtils.tokenize(alignment[1], ","));

		    StringBuilder strLineX = new StringBuilder();
		    StringBuilder strLineY = new StringBuilder();

		    for (final int nLineX : nLinesX)
			strLineX.append(sentencesX.getLine(nLineX) + " ");
		    for (final int nLineY : nLinesY)
			strLineY.append(sentencesY.getLine(nLineY) + " ");

		    e.println(strLineX.toString().trim());
		    f.println(strLineY.toString().trim());
		    a.println(strLineX.toString().trim());
		    a.println(strLineY.toString().trim());
		    a.println("");
		} catch (RuntimeException e1) {
		    throw new RuntimeException("Error trying to align:\n"
			    + sentencesFileX.getAbsolutePath() + "\n"
			    + sentencesFileY.getAbsolutePath() + "\n"
			    + alignmentsFile.getAbsolutePath() + "\n" + line, e1);
		}
	    }
	}

	sentencesX.close();
	sentencesY.close();
	in.close();
    }

    public static void createAlignmedFile(File sentencesFileX, File sentencesFileY,
	    File alignmentsFile, File alignedFile) throws IOException {

	FileLineArray sentencesX = new FileLineArray(sentencesFileX, FileLineArray.Mode.READ);
	FileLineArray sentencesY = new FileLineArray(sentencesFileY, FileLineArray.Mode.READ, "GB2312");

	BufferedReader in = new BufferedReader(new FileReader(alignmentsFile));
	PrintWriter out = new PrintWriter(alignedFile);
	String line;
	while ((line = in.readLine()) != null) {

	    if (!line.contains("omitted") && !line.equals("")) {

		String[] alignment = StringUtils.split(line, " <=> ", 2);

		int[] nLinesX = StringUtils.toIntArray(StringUtils.tokenize(alignment[0], ","));
		int[] nLinesY = StringUtils.toIntArray(StringUtils.tokenize(alignment[1], ","));

		StringBuilder strLineX = new StringBuilder();
		StringBuilder strLineY = new StringBuilder();

		for (final int nLineX : nLinesX)
		    strLineX.append(sentencesX.getLine(nLineX) + " ");
		for (final int nLineY : nLinesY)
		    strLineY.append(sentencesY.getLine(nLineY) + " ");

		out.println(strLineX.toString().trim());
		out.println(strLineY.toString().trim());
		out.println();
	    }
	}

	sentencesX.close();
	sentencesY.close();
	in.close();
	out.close();
    }

    // TODO: figure out how to generate the TT and what format that needs to
    // be
    // in

    public static void main(String... args) throws Exception {
	if (args.length != 4) {
	    System.err.println("Usage: program <sentencesFilesX> <sentencesFilesY> <alignmentFiles> <alignedFilesExt>");
	    System.exit(1);
	}

	System.out.println("Finding sentences files for X...");
	File[] filesX = FileUtils.getFilesFromWildcard(args[0]);
	System.out.println(filesX.length + " files found.");

	if (filesX.length > 1)
	    throw new Error("Not guaranteed to be parallel for more than 1 file!");
	// TODO: sort files?

	System.out.println("Finding sentences files for Y...");
	File[] filesY = FileUtils.getFilesFromWildcard(args[1]);
	System.out.println(filesY.length + " files found.");

	System.out.println("Finding alignment files...");
	File[] filesAlignments = FileUtils.getFilesFromWildcard(args[2]);
	System.out.println(filesAlignments.length + " files found.");

	if (filesX.length != filesY.length || filesY.length != filesAlignments.length) {
	    System.err.println("ERROR: Must have the same number of sentence files and alignment files.");
	    System.exit(1);
	}

	String outExt = args[3];

	for (int i = 0; i < filesX.length; i++) {
	    String alignedFileName = StringUtils.substringBefore(filesX[i].getName(), ".") + outExt;
	    File alignedFile = new File(filesX[i].getParent(), alignedFileName);

	    System.out.println(filesX[i].getName() + " + " + filesY[i].getName() + " + "
		    + filesAlignments[i].getName() + " ==> " + alignedFileName);
	    createAlignmedFile(filesX[i], filesY[i], filesAlignments[i], alignedFile);
	}

	System.out.println("Done.");
    }
}
