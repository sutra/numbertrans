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
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

/**
 * Broadcasts commands to the <code>RemoteExecutorListener</code> along with a
 * unique key indicating that it is safe to accept the given command.
 */
public class RemoteExecutorBroadcaster {

    private final static int DEFAULT_CLIENT_NUM = 50;
    private final Vector<SimpleClient> clients = new Vector<SimpleClient>(DEFAULT_CLIENT_NUM);

    public RemoteExecutorBroadcaster(final String configFile) throws IOException {
	loadConfig(configFile);
    }

    private void loadConfig(final String configFile) throws IOException {
	// load client data from config file
	final BufferedReader in = new BufferedReader(new FileReader(configFile));
	for (String line = in.readLine(); line != null; line = in.readLine()) {
	    if (line.length() > 0) {
		final String host = line;
		final SimpleClient client = new SimpleClient(host, RemoteExecutorListener.PORT);
		try {
		    client.connect();
		    clients.add(client);
		} catch (ConnectionException e) {
		    System.err.println("Could not connect to host: " + host + "    -----   " + e.getCause().getMessage());
		}
	    }
	}
    }

    private void broadcast(final String commandLine) {
	// determine local host name
	String host = "UNKNOWN_HOST";
	try {
	    host = InetAddress.getLocalHost().getHostName();
	} catch (UnknownHostException e) {
	    System.err.println("Could not determine local host address");
	}

	final String message;
	if (commandLine.equals(RemoteExecutorListener.TYPE_KILL_ALL)) {
	    message = RemoteExecutorListener.KEY + " " + host + " "
		    + RemoteExecutorListener.TYPE_KILL_ALL;
	} else {
	    message = RemoteExecutorListener.KEY + " " + host + " "
		    + RemoteExecutorListener.TYPE_RUN + " " + commandLine;
	}

	for (final SimpleClient client : clients) {
	    client.sendMessage(message);
	}
    }

    public static void main(String[] args) {
	if (args.length < 2) {
	    System.err.println("Usage: program <config_file> <commandLine>");
	    System.exit(1);
	}

	final String configFile = args[0];
	final String commandLine = StringUtils.untokenize(args, 1);

	try {
	    final RemoteExecutorBroadcaster broadcaster = new RemoteExecutorBroadcaster(configFile);
	    broadcaster.broadcast(commandLine);
	} catch (IOException e) {
	    System.err.println("Encoutered error while reading config file.");
	    e.printStackTrace();
	}
    }

}
