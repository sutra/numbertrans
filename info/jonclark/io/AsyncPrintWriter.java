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
package info.jonclark.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * Write lines of a file coming from a multi-threaded application in a specified
 * order. This class is thread-safe.
 */
public class AsyncPrintWriter {

    // TODO: Create a way so that we don't waste large amounts of null
    // vector images on large files.
    // This will probably involve storing an offset for lines that have
    // already been written, then reallocating a smaller vector (by hacking
    // off the first few already-written null elements).

    final Vector<String> vLines;
    final PrintWriter out;

    private static final long WAIT_FOR_LINES = 250;

    boolean isWriting = false;

    public AsyncPrintWriter(int nInitialLines, int nGrowSize, File outputFile)
	    throws FileNotFoundException {
	vLines = new Vector<String>(nInitialLines, nGrowSize);
	out = new PrintWriter(outputFile);
    }

    /**
         * @param line
         * @param nRow
         *                ZERO-BASED row number
         */
    public void println(String line, int nRow) {
	synchronized (vLines) {
	    if(vLines.size() < nRow + 1)
		vLines.setSize(nRow + 1);
	    vLines.set(nRow, line);
	}
    }

    public void beginAsyncWrite() {
	if (isWriting) {
	    ; // do nothing
	} else {
	    isWriting = true;
	    Thread thread = new Thread(writer);
	    thread.start();
	}
    }

    private Runnable writer = new Runnable() {
	public void run() {
	    int i = 0;

	    // write lines until we reach a null, then wait to get more
	    // lines
	    while (isWriting) {
		String line;
		synchronized (vLines) {
		    if (i >= vLines.size()) {
			line = null;
		    } else {
			line = vLines.get(i);
		    }
		}
		if (line == null) {
		    try {
			Thread.sleep(WAIT_FOR_LINES);
		    } catch (InterruptedException e) {
			; // we don't really care about this one...
		    }
		} else {
		    out.println(line);
		    vLines.set(i, null); // release this memory
		    i++;
		}
	    }

	    // now write all lines, including nulls
	    synchronized (vLines) {
		while (i < vLines.size()) {
		    String line = vLines.get(i);
		    if (line == null) {
			out.println();
		    } else {
			out.println(line);
		    }
		    i++;
		}
	    }

	    out.flush();
	    out.close();
	}
    };

    public void close() {
	isWriting = false;
    }

    public static void main(String[] args) throws Exception {
	AsyncPrintWriter writer = new AsyncPrintWriter(500, 10, new File("c:/test.txt"));
	writer.beginAsyncWrite();

	Thread.sleep(500);
	writer.println("0", 0);
	Thread.sleep(500);
	writer.println("C", 3);
	Thread.sleep(500);
	writer.println("D", 4);
	Thread.sleep(500);
	writer.println("E", 5);
	Thread.sleep(500);
	writer.println("A", 1);
	Thread.sleep(500);
	writer.println("B", 2);
	Thread.sleep(5000);
	writer.println("F", 6);
	Thread.sleep(500);
	writer.println("G", 7);
	Thread.sleep(500);
	writer.println("H", 8);
	Thread.sleep(500);

	writer.close();
    }
}
