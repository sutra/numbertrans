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
package info.jonclark.clientserver;

import info.jonclark.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Logger;

public abstract class TaskWorker extends SimpleServer {

    private final String knownTask;
    private boolean running = true;
    private final Logger log;

    public TaskWorker(String knownTask, int port, int nMaxConcurrentTasks, Logger log) {
	super(port, nMaxConcurrentTasks);

	this.knownTask = knownTask;
	this.log = log;
    }

    @Override
    public void handleClientRequest(Socket sock) {
	try {
	    final BufferedReader in = new BufferedReader(new InputStreamReader(sock
		    .getInputStream()));
	    final PrintWriter out = new PrintWriter(sock.getOutputStream());

	    // tell what task I can do
	    out.println("CAN: " + knownTask);
	    out.flush();

	    String line = null;
	    while (running && (line = in.readLine()) != null) {
		log.finer("Got line: " + line);
		
		try {
		    // get task and do it
		    String[] tokens = StringUtils.tokenize(line, " ", 2);
		    if (tokens[0].equals("TASK:")) {
			log.finer("Performing task: " + tokens[0]);
			this.performTask(tokens[1]);

			// reply with success
			out.println("RESULT: TRUE");
			out.flush();
		    } else {
			log.info("Unknown message type: " + tokens[0]);
		    }

		} catch (Throwable t) {
		    // CATCH **ANYTHING** THAT COMES OUT OF THIS LOOP SO
		    // THAT OUR WORKER DOESN'T DIE
		    out.println("RESULT: FALSE");
		    out.flush();
		    dieAndRespawn();
		}
	    }

	} catch(SocketException e) {
	    ; // this is probably just the connection being reset
	} catch (IOException e) {
	    log.severe("IOException while handling task.");
	    log.severe(StringUtils.getStackTrace(e));
	}
	log.finer("Ending client connection.");
    }

    /**
         * Do the requested task.
         * 
         * @param task
     * @throws Exception 
         */
    public abstract void performTask(final String task) throws Exception;

    /**
         * Something bad happened while handling a client request. Kill ALL
         * state information for this worker and start fresh.
         */
    public abstract void dieAndRespawn();

}
