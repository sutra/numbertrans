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
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * A simple asynchronous multi-threaded server.
 * 
 * @author Jonathan Clark
 */
public abstract class SimpleServer {
    private boolean shutdownRequested = false;
    private int port;
    private final int nMaxConnections;
    private AtomicInteger nCurrentConnections = new AtomicInteger(0);
    private final Logger log = LogUtils.getLogger();
    private final String encodingName;

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private ServerSocket serverSocket;
    private final SimpleServer parent = this;
    private Thread serverThread = null;

    public static final int SHUTDOWN_TIMEOUT_SECS = 10;

    /**
         * Create a new SimpleServer object.
         * 
         * @param port
         *                The port on which this server will listen.
         */
    public SimpleServer(final int port) {
	this(port, Integer.MAX_VALUE);
    }

    /**
         * Create a new SimpleServer object.
         * 
         * @param port
         *                The port on which this server will listen.
         * @param nMaxConnections
         *                The maximum number of simultaneous connections this
         *                server will accept. *
         */
    public SimpleServer(final int port, final int nMaxConnections) {
	this(port, nMaxConnections, null);
    }

    /**
         * Create a new SimpleServer object.
         * 
         * @param port
         *                The port on which this server will listen.
         * @param nMaxConnections
         *                The maximum number of simultaneous connections this
         *                server will accept. *
         * @param encodingName
         *                The name of the encoding to be used over the socket
         *                connection.
         */
    public SimpleServer(final int port, final int nMaxConnections, final String encodingName) {
	this.port = port;
	this.nMaxConnections = nMaxConnections;
	this.encodingName = encodingName;
    }

    public void stopServer() {
	shutdownRequested = true;
    }

    protected void switchToPort(int port) throws IOException {
	this.port = port;
	runServer();
    }

    public void runServer() throws IOException {
	if (serverThread != null && serverThread.isAlive() && !serverThread.isInterrupted())
	    serverThread.interrupt();
	serverSocket = new ServerSocket(port);
	serverThread = new Thread(serverRunnable);
	serverThread.start();
    }

    private Runnable serverRunnable = new Runnable() {

	public void run() {
	    try {
		while (!shutdownRequested) {
		    final Socket connection = parent.serverSocket.accept();
		    if (nCurrentConnections.get() < nMaxConnections) {

			// we will accept this connection
			Runnable r = new Runnable() {
			    public void run() {
				synchronized (parent) {
				    nCurrentConnections.getAndIncrement();
				}

				try {
				    final SimpleClient client = new SimpleClient(connection,
					    encodingName);
				    client.connect();
				    handleClientRequest(client);
				} catch (ConnectionException e) {
				    log.warning(StringUtils.getStackTrace(e));
				}

				synchronized (parent) {
				    nCurrentConnections.getAndDecrement();
				}
			    }
			};
			executor.execute(r);

		    } else {
			// we will reject this connection
			PrintWriter out = new PrintWriter(connection.getOutputStream());
			out.println("ERROR: nMaxConnections exceeded");
			out.flush();
			out.close();
			connection.close();
		    } // end if max connections reached
		}
		// Complete outstanding
		// requests before exiting
		executor.shutdown();
		try {
		    executor.awaitTermination(SHUTDOWN_TIMEOUT_SECS, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		    System.err.println("Interrupted while shutting down.");
		}
	    } catch (IOException e) {
		log.severe(StringUtils.getStackTrace(e));
	    }
	}
    };

    public abstract void handleClientRequest(SimpleClient client);

}
