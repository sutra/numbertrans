package info.jonclark.corpus.parallel;

import info.jonclark.util.FileUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Aligns parallel web pages based on their HTML structure. This version is
 * designed to work on TWO parallel pages, as opposed to parallel text within
 * the same page.
 * 
 * @author Jonathan
 */
public class HtmlParallelAligner {
    private static final String[] blockTags = { "h1", "h2", "h3", "h4", "h5", "div", "span", "td" };
    private static final String[] virtualBlockTags = { "br" };

    private class Block {
	public String blockTag;
	public String content;
	public final ArrayList<Block> children = new ArrayList<Block>();
	public boolean isAligned = false;

	public Block(String blockTag, String content) {
	    this.blockTag = blockTag;
	    this.content = content;
	}

	/**
         * Once any child has been aligned, we don't want to re-align its
         * parent, too
         */
	public boolean hasAlignedChild() {
	    for (final Block child : children)
		if (child.isAligned || child.hasAlignedChild())
		    return true;
	    return false;
	}
    }

    public boolean isBlockMatch(final Block foriegnBlock, final Block englishBlock) {
	// Check for matching numbers
	// Check for matching dictionary hits
	return false;
    }

    public void alignBlocks(final ArrayList<Block> englishBlocks,
	    final ArrayList<Block> foreignBlocks) {
	// TODO: make sure we don't need to "shift" the other direction

	// IDEA: First, do a type of bi-partite matching to find best
        // combinations
	// then search for trend

	boolean aligned = false;
	int nEnglishBlock = 0;
	int nForeignBlock = 0;
	while (!aligned) {
	    if (isBlockMatch(englishBlocks.get(nEnglishBlock), foreignBlocks.get(nForeignBlock))) {

	    }
	}
    }

    /**
         * @param html
         *                Full HTML text of a page
         * @return
         */
    public ArrayList<Block> segmentPageIntoBlocks(final String html) {
	// TODO: create "virtual block" from area between any two <br> tags

	final ArrayList<Block> list = new ArrayList<Block>();
	final String tag = "<br>";

	int nLastBr = html.indexOf(tag) + tag.length();
	int nCurrentBr = -1;
	while ((nCurrentBr = html.indexOf(tag, nLastBr)) != -1) {
	    final String content = html.substring(nLastBr, nCurrentBr);
	    final Block block = new Block(tag, content);
	    list.add(block);

	    // advance past the tag for the substring
	    nCurrentBr += tag.length();
	    nLastBr = nCurrentBr;
	}

	return list;
    }

    public static void main(String... args) throws Exception {
	HtmlParallelAligner a = new HtmlParallelAligner();
	ArrayList<Block> blocks = a.segmentPageIntoBlocks(FileUtils.getFileAsString(new File(
		args[0])));

	for (final Block block : blocks) {
	    System.out.println("*******************BLOCK************");
	    System.out.println(block.content + "\n\n\n\n\n");
	}
    }
}
