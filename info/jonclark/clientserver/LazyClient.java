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
import info.jonclark.util.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

/**
 * This client serves as a non-critical part of the application.
 * That is, messages sent to this client may or may not reach
 * the client if it is not reachable. Errors are ignored and
 * a certain number of messages will be sent to the client once
 * a connection is re-established. If the client is unreachable
 * or the connection is dropped, the connection will periodically
 * be retried.
 */
public class LazyClient implements ClientInterface {
    private final String host;
    private final int port;
    private Socket sock;
    private PrintWriter out;
    private BufferedReader in;
    private boolean connected = false;
    private ArrayList<ReconnectionListener> vListeners = new ArrayList<ReconnectionListener>();
    private final Logger log = LogUtils.getLogger();
    
    private final int nRetryTimeoutSec;
    
    /**
     * 
     * @param host
     * @param port
     * @param nRetryTimeoutSec how many seconds to wait between retries
     */
    public LazyClient(String host, int port, int nRetryTimeoutSec, Logger parent) {
        log.setParent(parent);
	    this.host = host;
	    this.port = port;
	    this.nRetryTimeoutSec = nRetryTimeoutSec;
    }
    
    public void connect() throws ConnectionException {
        try {
            log.fine("Connecting to " + host + " on port " + port);
	        sock = new Socket(host, port);
	        sock.setKeepAlive(true);
		    out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
		    in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		    connected = true;
        } catch(IOException ioe) {
            log.fine(StringUtils.getStackTrace(ioe));
            throw new ConnectionException("Could not connect to host.", ioe);
        }
    }
    
    public void sendMessage(String str) {
        if(connected) {
	        assert out != null;
	        out.println(str);
	        out.flush();
        } else {
            reconnect();
        }
    }
    
    public void disconnect() {
        if(connected) {
            connected = false;
            out.close();
        }
    }
    
    public void reconnect() {
        disconnect();
        Thread t = new Thread() {
            public void run() {
                // keep trying to reconnect to the server
                
                boolean success = false;
                while(!success) {
	                try {
	                    connect();
	                    success = true;
	                } catch(ConnectionException e) {
	                    success = false;
	                    try {
	                        Thread.sleep(nRetryTimeoutSec*1000);
	                    } catch(InterruptedException ie) {
	                        ; // we really don't care...
	                    }
	                }
                }
                // we are now reconnected
                notifyReconnectionListeners();
            }
        };
        t.start();
    }
    
    public void addReconnectionListener(ReconnectionListener r) {
        vListeners.add(r);
    }
    
    private void notifyReconnectionListeners() {
        for(ReconnectionListener r : vListeners)
            r.connectionReestablished(this);
    }

    /**
     * Returns the message from the client, if connected.
     * Otherwise, returns the empty string.
     */
    public String getMessage() {
        if(connected) {
	        assert in != null;
	        try {
	            return in.readLine();
	        } catch(IOException ioe) {
	            // we will attempt to reconnect
	        }
        }
        reconnect();
        return "";
    }
}
