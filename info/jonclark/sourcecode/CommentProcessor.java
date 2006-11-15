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
 * Separates comments from non-comments in Java source code. Allows
 * each of these pieces to be processed separately and then reconstituted
 * in an output file. This class's 2 abstract methods process the
 * types of text.
 */
public abstract class CommentProcessor {
    
    /**
     * Process a line of source code while separating the comments
     * from the non-comments.
     * 
     * @param out
     * @param line
     * @param inComment
     * @return
     */
    public String processLine(String line) {
        // TODO: Move this file to a generic comment handler
        // TODO: optimize this so that the builder isn't used if there is no comment

        boolean inComment = false; // are we inside a /* */ style comment?
        final StringBuilder builder = new StringBuilder();

        // handle multiple /* */ /* */ on one line
        while (line.indexOf("/*") != -1) {
            // detect the beginning of a /* */ comment
            if (!inComment && line.indexOf("/*") != -1) {
                builder.append(processNoncomment(StringUtils.substringBefore(
                        line, "/*", false)));
                line = processComment(StringUtils.substringAfter(line, "/*",
                        true));
                inComment = true;
            }

            // detect the end of a /* */ comment
            if (inComment && line.indexOf("*/") != -1) {
                builder.append(processComment(StringUtils.substringBefore(line,
                        "*/", true)));

                line = StringUtils.substringAfter(line, "*/", false);
                if (line.indexOf("/*") == -1)
                    line = processNoncomment(line);
                inComment = false;
            }
        }

        if (inComment) {
            // handle the continuations of /* * */ on multiple lines
            builder.append(processComment(line));
        } else {
            // detect a // style comment (could be on the same line as a /* */
            // comment
            final String nonComment = StringUtils.substringBefore(line, "//",
                    false);
            final String comment = StringUtils.substringAfter(line, "//", true);
            builder.append(processNoncomment(nonComment)
                    + processComment(comment));
        }

        return builder.toString();
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
