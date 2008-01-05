/*
 * Copyright (c) 2007, Jonathan Clark <jon_DOT_h_DOT_clark_AT_gmail_DOT_com> 
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
package info.jonclark.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * Allows for treating a text file encoded in UTF-8 or ASCII as an array of
 * lines
 */
public class FileLineArray {

    /**
     * ORIGINAL (LESS EFFICIENT) IMPLEMENTATION private final Vector<String>
     * vLines = new Vector<String>(1000, 1000); public
     * FileLineArray(InputStream inputStream) throws IOException {
     * BufferedReader in = new BufferedReader(new
     * InputStreamReader(inputStream)); String line; while( (line =
     * in.readLine()) != null) { vLines.add(line); } } public String getLine(int
     * nLine) { return vLines.get(nLine); }
     */

    public enum Mode {
	READ, READ_WRITE, READ_WRITE_NO_METADATA, READ_WRITE_WITH_METADATA
    };

    private final ArrayList<Long> vNewLines = new ArrayList<Long>(10000);
    private final RandomAccessFile raFile;
    private int nCurrentLine;
    private final String encoding;

    public FileLineArray(File file, Mode mode) throws IOException {
	this(file, mode, System.getProperty("file.encoding"));
    }

    public FileLineArray(File file, Mode mode, String encoding) throws IOException {
	this.encoding = encoding;
	String strMode = getModeString(mode);

	this.raFile = new RandomAccessFile(file, strMode);
	byte[] buf = new byte[1024];

	long offset = 0;
	int nRead = 0;
	while ((nRead = raFile.read(buf)) != -1) {
	    for (int i = 0; i < nRead; i++) {

		// use the properties of UTF-8 to our advantage:
		// no ASCII character can appear in a multi-byte set

		// add the position AFTER each newline character
		// TODO: Deal with other possible types of newlines
		if (buf[i] == 0x0A)
		    vNewLines.add(offset + i + 1);
	    }
	    offset = raFile.getFilePointer();
	}

	nCurrentLine = 0;
	raFile.seek(0);
    }

    private static String getModeString(Mode mode) {
	if (mode == Mode.READ) {
	    return "r";
	} else if (mode == Mode.READ_WRITE) {
	    return "rw";
	} else if (mode == Mode.READ_WRITE_NO_METADATA) {
	    return "rws";
	} else if (mode == Mode.READ_WRITE_WITH_METADATA) {
	    return "rwd";
	} else {
	    throw new RuntimeException("Unknown mode: " + mode);
	}
    }

    /**
     * @param nLine
     *                Zero-based line number
     * @return
     * @throws IOException
     */
    public String getLine(int nLine) throws IOException {
	// TODO: Use lazy instantiation: Don't index the file until a certain
	// line number is requested.

	nCurrentLine = nLine;

	long nFirstByte;
	if (nLine == 0)
	    nFirstByte = 0;
	else
	    nFirstByte = vNewLines.get(nLine - 1);

	long nLastByte;
	if (nLine < vNewLines.size())
	    nLastByte = vNewLines.get(nLine);
	else
	    nLastByte = raFile.length() + 1;

	// return the string without the newline
	int nByteCount = (int) (nLastByte - nFirstByte - 1);
	byte[] bytes = new byte[nByteCount];

	raFile.seek(nFirstByte);
	raFile.readFully(bytes);

	return new String(bytes, encoding);
    }

    public void seekToLine(int nLine) throws IOException {
	nCurrentLine = nLine;
	if (nLine == 0) {
	    raFile.seek(0);
	} else {
	    long nFirstByte = vNewLines.get(nLine - 1);
	    raFile.seek(nFirstByte);
	}
    }

    /**
     * Read a line and increment the current line number.
     * 
     * @return
     * @throws IOException
     */
    public String readLine() throws IOException {
	nCurrentLine++;
	return this.getLine(nCurrentLine);
    }

    /**
     * @return The line number of the line that would be returned by a call to
     *         <code>readLine()</code>
     */
    public int getNextLineNumber() {
	return nCurrentLine;
    }

    public int getLineCount() {
	return vNewLines.size() + 1;
    }

    public void close() throws IOException {
	this.raFile.close();
    }

    public static void main(String... args) throws Exception {
	FileLineArray arr = new FileLineArray(new File("conf/notify.properties"), Mode.READ);
	System.out.println(arr.getLine(0));
	System.out.println(arr.getLine(2));
	System.out.println(arr.getLine(6));
    }

}
