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
package info.jonclark.gui;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

import javax.swing.*;

import info.jonclark.clientserver.*;
import info.jonclark.util.*;

/**
 * A progress server (monitor) accepts connections from various progress clients
 * that are currently working on tasks. This provides the user of the server a
 * central way to keep track of several ongoing tasks
 */
public class ProgressServer extends SimpleServer {
    private BasicSwingApp frame = new BasicSwingApp("Progress Server");
    private JPanel contentPanel = new JPanel();
    private int nStallTimeoutSec = -1;
    private final Logger log = Logger.getLogger("info.jonclark.gui.ProgressServer");
    private Hashtable<String, ProgressPanel> pans = new Hashtable<String, ProgressPanel>();
    private Hashtable<Socket, String> sockets = new Hashtable<Socket, String>();

    public ProgressServer(Properties props, Logger parent) throws IOException {
	super(Integer.parseInt(props.getProperty("notifyServer.port", "4242")));
	log.setParent(parent);
	frame.setSize(250, 100);
	frame.getContentPane().add(contentPanel);
	runServer();
    }

    /*
         * (non-Javadoc)
         * 
         * @see info.jonclark.clientserver.SimpleServer#handleClientRequest(java.net.Socket)
         */
    public void handleClientRequest(Socket sock) {
	BufferedReader in = null;
	try {
	    in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	    // PrintWriter out = new PrintWriter(new
                // OutputStreamWriter(sock.getOutputStream()));

	    log.info("Got new connection from: " + sock.getInetAddress().getHostName());

	    // determine if we already have a client with this name

	    String line = null;
	    while ((line = in.readLine()) != null) {
		String[] tokens = StringUtils.tokenize(line);
		StringUtils.internTokens(tokens);
		if (tokens.length < 2) {
		    log.warning("Received less than 2 tokens: " + line);
		    continue;
		}

		String command = tokens[0];
		String name = tokens[1];

		// make sure we don't get multiple clients
		// with the same name (this is necessary because
		// we allow reconnections)
		if (sockets.containsKey(sock)) {
		    if (sockets.get(sock) != name) {
			JOptionPane.showInternalMessageDialog(frame,
				"Duplicate task name from incoming client. Ignoring connection.",
				"Duplicate Name", JOptionPane.ERROR_MESSAGE);
			// trap this connection since it will persistently
			// try to reconnect otherwise
			while (true) {
			    // FIXME: This could have undesirable results
			    Thread.sleep(Long.MAX_VALUE);
			}
		    }
		} else {
		    sockets.put(sock, name);
		}

		ProgressPanel pan = getProgressPanel(name);

		// NOTE: Intern has been used so that equalities may
		// be expressed as ==
		// if(command == "NAME") {
		// if(tokens.length == 2) {
		// getProgressPanel(name);
		// } else {
		// log.warning("Wrong number of arguments: " + line);
		// }
		// } else
		if (command == "MAX") {
		    if (tokens.length == 3) {
			String value = tokens[2];
			pan.setMaxValue(Integer.parseInt(value));
		    } else {
			log.warning("Wrong number of arguments: " + line);
		    }
		} else if (command == "STAT") {
		    if (tokens.length == 3) {
			String stat = tokens[2];
			pan.setProgress(Integer.parseInt(stat));
			log.finest("Status is now: " + stat);
		    } else {
			log.warning("Wrong number of arguments: " + line);
		    }
		} else if (command == "DONE") {
		    if (tokens.length == 3) {
			String strSuccess = tokens[2];
			pan.setDone(Boolean.parseBoolean(strSuccess));
			log.finer("Task is done. Success: " + strSuccess);
		    } else {
			log.warning("Wrong number of arguments: " + line);
		    }
		} else if (command == "STALL") {
		    if (tokens.length == 3) {
			String timeout = tokens[2];
			pan.setStallTimeout(Integer.parseInt(timeout));
			log.fine("Setting stall timeout to " + timeout + " seconds");
		    } else {
			log.warning("Wrong number of arguments: " + line);
		    }
		} else {
		    log.warning("Unknown message: " + line);
		}
	    }
	} catch (IOException ioe) {
	    log.warning(StringUtils.getStackTrace(ioe));
	} catch (Exception e) {
	    // TODO: Set status as terminated here. (leave progress bar as
                // is)
	    log.warning(StringUtils.getStackTrace(e));
	}
	try {
	    if (in != null)
		in.close();
	} catch (IOException ioe) {
	    log.warning(StringUtils.getStackTrace(ioe));
	}
	sockets.remove(sock);
    }

    /**
         * Finds a progress panel in a Hashtable of ProgressPanels. If the panel
         * does not yet exist, it is created.
         * 
         * @param pans
         *                Hashtable of ProgressPanels, keyed by name
         * @param name
         *                The name of the ProgressPanel to be returned
         * @return
         */
    protected ProgressPanel getProgressPanel(String name) {
	synchronized (pans) {
	    ProgressPanel pan = pans.get(name);
	    if (pan == null) {
		log.fine("Creating new panel with name: " + name);
		pan = new ProgressPanel(name, log);
		pans.put(name, pan);
		contentPanel.add(pan);
		frame.pack();
		frame.validate();
	    } else {
		pan.setName(name);
	    }
	    return pan;
	}
    }

    public static void main(String[] args) throws Exception {
	// GuiUtils.setNativeLookAndFeel();
	Logger log = Logger.getAnonymousLogger();
	Handler hand = new ConsoleHandler();
	hand.setLevel(Level.ALL);
	log.addHandler(hand);
	log.setLevel(Level.ALL);

	Properties props = PropertyUtils.getProperties("conf/notifyServer.properties");
	ProgressServer prog = new ProgressServer(props, log);
    }
}
