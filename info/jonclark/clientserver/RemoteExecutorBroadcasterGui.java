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

import java.awt.Button;
import java.awt.Choice;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Broadcasts commands to the <code>RemoteExecutorListener</code> along with a
 * unique key indicating that it is safe to accept the given command.
 */
public class RemoteExecutorBroadcasterGui extends Frame {

    /**
     * 
     */
    private static final long serialVersionUID = 4205112995693089987L;
    private final static int DEFAULT_CLIENT_NUM = 50;
    private final ArrayList<SimpleClient> clients = new ArrayList<SimpleClient>(DEFAULT_CLIENT_NUM);

    private final TextArea textCommand = new TextArea();
    private final Choice comboSet = new Choice();
    
    private void init() {
	this.setTitle("Remote Executor Broadcaster");
	final Panel panel = new Panel(new GridLayout(10,1));
	this.add(panel);
	
	comboSet.add("all");
	comboSet.add("clients.ini");
	
	panel.add(new Label("Config file"));
	panel.add(comboSet);
	panel.add(new Label("Command"));
	panel.add(textCommand);
	
	final Button bSend = new Button("Broadcast");
	bSend.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		broadcast(textCommand.getText());
	    }
	});
	panel.add(bSend);
	
	this.addWindowListener(new WindowListener() {
	    public void windowActivated(WindowEvent e) {}
	    public void windowClosed(WindowEvent e) {}
	    public void windowClosing(WindowEvent e) {
		System.exit(0);
	    }
	    public void windowDeactivated(WindowEvent e) {}
	    public void windowDeiconified(WindowEvent e) {}
	    public void windowIconified(WindowEvent e) {}
	    public void windowOpened(WindowEvent e) {}
	});
	this.setSize(250, 250);
	this.setVisible(true);
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
		    System.err.println("Could not connect to host: " + host + "    -----   "
			    + e.getCause().getMessage());
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
	final RemoteExecutorBroadcasterGui broadcaster = new RemoteExecutorBroadcasterGui();
	broadcaster.init();
    }

}
