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

import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import info.jonclark.clientserver.SimpleClient;
import info.jonclark.clientserver.SimpleServer;
import info.jonclark.log.LogUtils;
import info.jonclark.properties.PropertyUtils;
import info.jonclark.util.StringUtils;

/**
 * A progress server (monitor) accepts connections from various progress clients
 * that are currently working on tasks. This provides the user of the server a
 * central way to keep track of several ongoing tasks
 */
public class ProgressServer extends SimpleServer {
    private BasicSwingApp frame = new BasicSwingApp("Progress Server");
    private JPanel contentPanel = new JPanel();
    private int nStallTimeoutSec = -1;
    private final Logger log = LogUtils.getLogger();
    private Hashtable<String, ProgressPanel> pans = new Hashtable<String, ProgressPanel>();
    private Hashtable<SimpleClient, String> clients = new Hashtable<SimpleClient, String>();

    public ProgressServer(Properties props) throws IOException {
	super(Integer.parseInt(props.getProperty("notifyServer.port", "4242")));
	frame.setSize(250, 100);
	frame.getContentPane().add(contentPanel);
	runServer();
    }

    /*
         * (non-Javadoc)
         * 
         * @see info.jonclark.clientserver.SimpleServer#handleClientRequest(java.net.Socket)
         */
    public void handleClientRequest(SimpleClient client) {
	try {
	    // PrintWriter out = new PrintWriter(new
                // OutputStreamWriter(sock.getOutputStream()));

	    log.info("Got new connection from: " + client.getHost());

	    // determine if we already have a client with this name

	    String line = null;
	    while ((line = client.getMessage()) != null) {
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
		if (clients.containsKey(client)) {
		    if (clients.get(client) != name) {
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
		    clients.put(client, name);
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
	} catch (Exception e) {
	    // TODO: Set status as terminated here. (leave progress bar as
                // is)
	    log.warning(StringUtils.getStackTrace(e));
	}
	client.disconnect();
	clients.remove(client);
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
	LogUtils.logAll();

	Properties props = PropertyUtils.getProperties("conf/clientserver/notifyServer.properties");
	ProgressServer prog = new ProgressServer(props);
    }
}
