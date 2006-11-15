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

/**
 * A simple asynchronous client. <b>(INCOMPLETE).</b> Stopped work on this
 * class due to the case of handling multi-line messages. In this case, the
 * calling application should actually modify the synchronous client.
 * 
 * @author Jonathan Clark
 */
public abstract class SimpleAsyncClient implements Runnable {

    private final ClientInterface client;

    /**
         * Create a new SimpleAsynchClient object. Recommended usage:
         * <code>new SimpleAsyncClient(new SimpleClient(host,port));</code>
         * 
         * @param client
         *                The client to which we want to apply this asynchronous
         *                processing.
         */
    public SimpleAsyncClient(ClientInterface client) {
	this.client = client;
    }

    public void connect() throws ConnectionException {
	client.connect();
    }
    
    public void disconnect() {
	client.disconnect();
    }
    
    public void sendMessage(final String str) throws ConnectionException {
	client.sendMessage(str);
    }
    
    /**
         * Deal with a message received from the server in an asynchronous
         * fashion.
         * 
         * @param str
         *                The message received from the server (one line).
         */
    public abstract void handleRequest(final String str);
}
