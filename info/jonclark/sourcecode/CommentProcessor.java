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
package info.jonclark.sourcecode;

import info.jonclark.util.StringUtils;

/**
 * Separates comments from non-comments in Java source code. Allows each of
 * these pieces to be processed separately and then reconstituted in an output
 * file. This class's 2 abstract methods process the types of text.
 */
public abstract class CommentProcessor {

	private final boolean trackOutput;
	private final boolean provideInput;
	private boolean inComment = false; // are we inside a /* */ style comment?

	public CommentProcessor() {
		this.trackOutput = true;
		this.provideInput = true;
	}

	/**
	 * @param trackOutput
	 *            Return non-null values from processing?
	 */
	public CommentProcessor(boolean trackOutput) {
		this.trackOutput = trackOutput;
		this.provideInput = true;
	}

	/**
	 * @param trackOutput
	 *            Return non-null values from processing?
	 * @param provideInput
	 *            Give input to processComment() and processNoncomment()? This
	 *            might be useful in cases where the user only cares about the
	 *            number of comments and non-comments encountered.
	 */
	public CommentProcessor(boolean trackOutput, boolean provideInput) {
		this.trackOutput = trackOutput;
		this.provideInput = provideInput;
	}

	/**
	 * Process a line of source code while separating the comments from the
	 * non-comments.
	 * 
	 * @param out
	 * @param line
	 * @param inComment
	 * @return
	 */
	public String processLine(String line) {
		// TODO: optimize this so that the builder isn't used if there is no
		// comment
		StringBuilder builder = null;
		if (trackOutput)
			builder = new StringBuilder();

		// handle multiple /* */ /* */ on one line
		while (!inComment && line != null && line.contains("/*") || inComment && line != null
				&& line.contains("*/")) {
			// detect the beginning of a /* */ comment
			if (!inComment && line.contains("/*")) {
				final String nonComment = StringUtils.substringBefore(line, "/*", false);
				if (nonComment.length() > 0) {
					final String nonCommentResult = processNoncomment(nonComment);
					if (trackOutput)
						builder.append(nonCommentResult);
				}

				String comment = null;
				if (provideInput)
					StringUtils.substringAfter(line, "/*", true);

				if (trackOutput) {
					line = processComment(comment);
					if (line != null && line.trim().startsWith("/*"))
						line = line.trim().substring(2);
				} else {
					processComment(null);
					final String remainder = StringUtils.substringAfter(line, "/*", false);
					line = remainder;
				}
				inComment = true;
			}

			// detect the end of a /* */ comment
			if (inComment && line != null && line.contains("*/")) {
				String comment = null;
				if (provideInput)
					comment = StringUtils.substringBefore(line, "*/", true);
				final String commentResult = processComment(comment);
				if (trackOutput)
					builder.append(commentResult);

				line = StringUtils.substringAfter(line, "*/", false);
				if (!line.contains("/*")) {
					String nonComment = line;
					if (nonComment.length() > 2) {
						final String nonCommentResult = processNoncomment(nonComment);
						if (trackOutput)
							line = nonCommentResult;
					}
				}
				inComment = false;
			}
		}

		// TODO: make sure lines with combination comments are handled correctly
		if (inComment) {

			// handle the continuations of /* * */ on multiple lines
			String comment = null;
			if (provideInput)
				comment = line;
			final String commentResult = processComment(comment);
			if (trackOutput)
				builder.append(commentResult);

		} else {
			// detect a // style comment (could be on the same line as a /*
			// */ comment
			if (line.contains("//")) {
				final String nonComment = StringUtils.substringBefore(line, "//", false);
				String comment = null;
				if (provideInput)
					comment = StringUtils.substringAfter(line, "//", true);

				String nonCommentResult = null;
				if (nonComment.length() > 0)
					nonCommentResult = processNoncomment(nonComment);
				final String commentResult = processComment(comment);

				if (trackOutput)
					builder.append(nonCommentResult + commentResult);
			} else {
				final String nonComment = line;
				if (nonComment.length() > 0) {
					final String nonCommentResult = processNoncomment(nonComment);
					if (trackOutput)
						builder.append(nonCommentResult);
				}
			}
		}

		if (trackOutput)
			return builder.toString();
		else
			return null;
	}

	public boolean isInComment() {
		return inComment;
	}

	/**
	 * Process the comment (including the comment markers)
	 * 
	 * @param line
	 * @return
	 */
	protected abstract String processComment(String line);

	/**
	 * Process the non-comment portion (excluding comment markers)
	 * 
	 * @param line
	 * @return
	 */
	protected abstract String processNoncomment(String line);

}
