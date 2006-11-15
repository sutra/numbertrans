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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import info.jonclark.util.StringUtils;

/**
 * Escape characters that are unicode within code. Skips any character that are
 * within comments.
 */
public class UnicodeEscaper extends CommentProcessor {

    /**
     * Escape all unicode characters not in comment within a Java source file
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        final UnicodeEscaper ue = new UnicodeEscaper();
        
        final BufferedReader in = new BufferedReader(new InputStreamReader(
                new FileInputStream(args[0]), "UTF-8"));
        final File f = new File(args[1]);
        f.createNewFile();
        final PrintWriter out = new PrintWriter(new FileWriter(f));

        String line;
        while ((line = in.readLine()) != null)
            out.println(ue.processLine(line));

        out.flush();
        out.close();
    }

    /**
     * Process the comment (including the comment markers)
     * 
     * @param line
     * @return
     */
    protected String processComment(String line) {
        return line;
    }

    /**
     * Process the non-comment portion (excluding comment markers)
     * 
     * @param line
     * @return
     */
    protected String processNoncomment(String line) {
        return StringUtils.escapeUnicode(line);
    }

}
