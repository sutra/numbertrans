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
package info.jonclark.util;

import info.jonclark.io.PipedStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ProcessUtils {

	@Deprecated
	public static void runProcessWait(final String commandLine) throws InterruptedException,
			IOException {
		runProcessWait(commandLine, false);
	}

	public static void runProcessWait(final String commandLine, boolean showOutput)
			throws InterruptedException, IOException {
		Process p = Runtime.getRuntime().exec(commandLine);
		if (showOutput) {
			sendStreamTo(p.getErrorStream(), System.err);
			sendStreamTo(p.getInputStream(), System.out);
		} else {
			sendStreamToBlackHole(p.getErrorStream());
			sendStreamToBlackHole(p.getInputStream());
		}
		p.wait();
	}

	public static void sendStreamToBlackHole(final InputStream in) {
		final StreamBlackHole hole = new StreamBlackHole(in);
		hole.start();
	}
	
	public static void sendStreamTo(final InputStream in, final OutputStream out) {
		final PipedStream pipe = new PipedStream(in, out);
		pipe.start();
	}

	/**
	 * Takes all input from the given stream and sends it into oblivion.
	 */
	private static class StreamBlackHole extends Thread {

		final InputStream in;

		public StreamBlackHole(InputStream in) {
			this.in = in;
		}

		public void run() {
			final BufferedReader br = new BufferedReader(new InputStreamReader(in));
			try {
				while (br.readLine() != null)
					;
			} catch (IOException e) {
				// we really don't care since this is a black hole
				e.printStackTrace();
			}
		}
	}
}
