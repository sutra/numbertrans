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

import info.jonclark.log.LogUtils;
import info.jonclark.util.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.logging.Logger;

public abstract class TaskWorker extends SimpleServer {

    private final String knownTask;
    private boolean running = true;
    private final Logger log = LogUtils.getLogger();

    public TaskWorker(String knownTask, int port, int nMaxConcurrentTasks) {
	super(port, nMaxConcurrentTasks);

	this.knownTask = knownTask;
    }

    @Override
    public void handleClientRequest(SimpleClient client) {
	try {
	    log.info("New connection from " + client.getHost());
	    // tell what task I can do
	    client.sendMessage("CAN: " + knownTask);

	    String line = null;
	    while (running && (line = client.getMessage()) != null) {
		log.finer("Got line: " + line);

		try {
		    // get task and do it
		    String[] tokens = StringUtils.tokenize(line, TaskMaster.GROUP_DELIM, 3);
		    final String command = tokens[0];
		    final String task = tokens[1];
		    if (command.equals("TASK:")) {
			log.finer("Performing task: " + task);
			String[] args;
			if (tokens.length == 3) {
			    args = StringUtils.tokenize(tokens[2], TaskMaster.ARG_DELIM);
			} else {
			    args = new String[0];
			}

			String[] arrResults = this.performTask(task, args);
			String results;
			if (arrResults == null) {
			    results = "";
			} else {
			    results = StringUtils.untokenize(arrResults, TaskMaster.ARG_DELIM);
			}

			// convert into a safe representation by escaping some
			// characters
			// and reply with success
			String reply = URLEncoder.encode("RESULT: TRUE " + results, "UTF-8");
			client.sendMessage(reply);
		    } else {
			log.info("Unknown message type: " + command);
		    }

		} catch (Throwable t) {
		    // CATCH **ANYTHING** THAT COMES OUT OF THIS LOOP SO
		    // THAT OUR WORKER DOESN'T DIE
		    log.severe("Error while performing task:");
		    log.severe(StringUtils.getStackTrace(t));

		    String reply = URLEncoder.encode("RESULT: FALSE "
			    + StringUtils.getStackTrace(t), "UTF-8");
		    client.sendMessage(reply);
		    dieAndRespawn();
		}
	    }
	} catch (IOException e) {
	    log.severe("IOException while handling task.");
	    log.severe(StringUtils.getStackTrace(e));
	} catch (ConnectionException e) {
	    log.warning(StringUtils.getStackTrace(e));
	}

	// Make every effot to tidy up.
	log.finer("Ending client connection.");
	client.disconnect();
    }

    /**
         * Do the requested task.
         * 
         * @param task
         * @throws Exception
         */
    public abstract String[] performTask(final String task, final String[] args) throws Exception;

    /**
         * Something bad happened while handling a client request. Kill ALL
         * state information for this worker and start fresh.
         */
    public abstract void dieAndRespawn();

}
