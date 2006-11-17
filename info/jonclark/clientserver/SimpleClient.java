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

import java.io.*;
import java.net.*;

/**
 * A simple synchronous socket client. This class contains asserts, so the Java
 * VM should be run with the option -ea during debugging.
 * 
 * @author Jonathan Clark
 */
public class SimpleClient implements ClientInterface {
    private final String host;
    private final int port;
    private PrintWriter out;
    private BufferedReader in;
    private final String encodingName;

    /**
         * Create a new SimpleClient object. This does NOT connect the client.
         * 
         * @param host
         *                The server to which we want to connect
         * @param port
         *                The port on which the server is listening
         */
    public SimpleClient(String host, int port) {
	this.host = host;
	this.port = port;
	this.encodingName = null;
    }

    /**
         * Create a new SimpleClient object with the specified encoding. This
         * does NOT connect the client.
         * 
         * @param host
         *                The server to which we want to connect
         * @param port
         *                The port on which the server is listening
         * @param encodingName
         *                The name of the encoding to be used over the socket
         *                connection.
         */
    public SimpleClient(String host, int port, String encodingName) {
	this.host = host;
	this.port = port;
	this.encodingName = encodingName;
    }

    /**
         * Send a message to the server.
         * 
         * @param str
         *                The string that will be immediately sent to the
         *                server.
         */
    public void sendMessage(final String str) {
	assert out != null : "Not connected.";

	out.println(str);
	out.flush();
    }

    /**
         * Close our connection to the server
         */
    public void disconnect() {
	if (out != null) {
	    out.flush();
	    out.close();
	}
	in = null;
	out = null;
    }

    /**
         * Connect to the server; this must be called before attempting to send
         * a message.
         * 
         * @throws ConnectionException
         */
    public void connect() throws ConnectionException {
	assert in == null && out == null : "Already connected.";

	try {
	    Socket sock = new Socket(host, port);

	    if (encodingName == null) {
		out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	    } else {
		out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), encodingName));
		in = new BufferedReader(new InputStreamReader(sock.getInputStream(), encodingName));
	    }
	} catch (IOException ioe) {
	    throw new ConnectionException("Could not connect to host.", ioe);
	}
    }

    /**
         * Read a message sent to us from the server. Blocks until a message is
         * read. <b>Note:</b> The incoming buffer associated with this message
         * call should be emptied in a timely manner.
         * 
         * @throws ConnectionException
         *                 If there was an error retrieving the message.
         */
    public String getMessage() throws ConnectionException {
	assert in != null : "Not connected.";

	try {
	    return in.readLine();
	} catch (IOException ioe) {
	    throw new ConnectionException("Could not get message.", ioe);
	}
    }

    public String getHost() {
	return host;
    }

    public int getPort() {
	return port;
    }

    public String toString() {
	return host + ":" + port;
    }
}
