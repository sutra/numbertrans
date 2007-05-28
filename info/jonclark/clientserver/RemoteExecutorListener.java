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
import info.jonclark.util.ProcessUtils;
import info.jonclark.util.StringUtils;

import java.io.IOException;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * Listens for commands from the <code>RemoteExecutorBroadcater</code> and
 * runs the command specified, provided the correct passkey is provided
 */
public class RemoteExecutorListener extends SimpleServer {

    private static final Logger log = LogUtils.getLogger();

    public RemoteExecutorListener(int port) {
	super(port);
    }

    private static final int NUM_TOKENS = 4;
    public static final String KEY = "ASDGFADfhsadlkjhqwetlh124907237asvdlkjhawerlkjg';l';k%%^#%$^#^%!1khaskfdljhadsfkjhFORTY_TWO";
    private static final String MASTER_HOST = "LAB28";

    public static final int PORT = 4242;
    public static final String TYPE_RUN = "RUN";
    public static final String TYPE_KILL = "KILL";
    public static final String TYPE_KILL_ALL = "KILL_ALL";

    private TreeMap<String, Process> processes = new TreeMap<String, Process>();

    private void run(String commandLine) {
	try {
	    final Process proc = Runtime.getRuntime().exec(commandLine);
	    ProcessUtils.sendStreamToBlackHole(proc.getInputStream());
	    ProcessUtils.sendStreamToBlackHole(proc.getErrorStream());
	    String processName = StringUtils.substringBefore(commandLine, " ");
	    processes.put(processName, proc);
	} catch (IOException e1) {
	    ;
	}
    }

    private void kill(String commandLine) {
	Process proc = processes.get(commandLine);
	if (proc != null) {
	    proc.destroy();
	} else {
	    log.warning("Process not found: " + commandLine);
	}
    }

    private void killAll() {
	for (Process proc : processes.values()) {
	    proc.destroy();
	}
    }

    @Override
    public void handleClientRequest(SimpleClient client) {
	try {
	    for (String line = client.getMessage(); line != null; line = client.getMessage()) {
		final String[] tokens = StringUtils.tokenize(line, " ", NUM_TOKENS);

		if (tokens.length < 3) {
		    System.err.println("Invalid number of tokens in request.");
		    continue;
		} else {
		    final String key = tokens[0];
		    final String masterHost = tokens[1];
		    final String commandType = tokens[2];

		    if (!key.equals(KEY)) {
			System.err.println("Invalid key.");
			continue;
		    }
		    if (!masterHost.equalsIgnoreCase(MASTER_HOST)) {
			System.err.println("Invalid master host: " + masterHost);
			continue;
		    }

		    // verification succeeded
		    if (commandType.equals(TYPE_RUN)) {
			final String commandLine = tokens[3];
			run(commandLine);
		    } else if (commandType.equals(TYPE_KILL)) {
			final String commandLine = tokens[3];
			kill(commandLine);
		    } else if (commandType.equals(TYPE_KILL_ALL)) {
			killAll();
		    }
		}

		if (line.startsWith(KEY)) {
		    String commandLine = line.substring(KEY.length()).trim();
		    if (commandLine.startsWith(MASTER_HOST)) {
			commandLine = commandLine.substring(MASTER_HOST.length());
			System.out.println("Executing: " + commandLine);
			run(commandLine);
		    } else {
			System.err.println("Invalid requesting host in request: " + commandLine);
		    }
		} else {
		    System.err.println("Invalid key in request: " + line);
		}
	    }
	} catch (ConnectionException e) {
	    log.warning(StringUtils.getStackTrace(e));
	}
    } // end handleClientRequest

    public static void main(String[] args) throws IOException {
	final RemoteExecutorListener listener = new RemoteExecutorListener(PORT);
	listener.runServer();
    }

}
