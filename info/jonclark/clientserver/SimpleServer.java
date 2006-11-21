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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple asynchronous multi-threaded server.
 * 
 * @author Jonathan Clark
 */
public abstract class SimpleServer {
    private boolean shutdownRequested = false;
    private final int port;
    private final int nMaxConnections;
    private AtomicInteger nCurrentConnections = new AtomicInteger(0);

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
         *                server will accept.
         */
    public SimpleServer(final int port, final int nMaxConnections) {
	this.port = port;
	this.nMaxConnections = nMaxConnections;
    }

    public void stopServer() {
	shutdownRequested = true;
    }

    public void runServer() throws IOException {
	final ExecutorService executor = Executors.newCachedThreadPool();
	final ServerSocket socket = new ServerSocket(port);
	final SimpleServer synchronizable = this;

	while (!shutdownRequested) {
	    final Socket connection = socket.accept();
	    if (nCurrentConnections.get() < nMaxConnections) {
		// we will accept this connection
		Runnable r = new Runnable() {
		    public void run() {
			synchronized (nCurrentConnections) {
			    nCurrentConnections.getAndIncrement();
			}
			handleClientRequest(connection);
			synchronized (synchronizable) {
			    nCurrentConnections.getAndDecrement();
			}
		    }
		};
		executor.execute(r);

	    } else {
		// we will reject this connection
		PrintWriter out = new PrintWriter(connection.getOutputStream());
		out.println("ERROR: nMaxConnections exceeded.");
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
    }

    public abstract void handleClientRequest(Socket sock);

}
