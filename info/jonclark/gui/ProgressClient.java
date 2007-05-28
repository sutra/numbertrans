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

import info.jonclark.clientserver.*;
import info.jonclark.log.LogUtils;
import info.jonclark.properties.PropertiesException;
import info.jonclark.properties.PropertyUtils;
import info.jonclark.util.*;

import java.util.*;
import java.util.logging.*;

/**
 * A client program that sends the status of its progress to a
 * ProgressServer or group of ProgressServers, if possible.
 * If a connection cannot be made to one or more of the progress
 * servers, the program attempts to connect to them at regular
 * intervals while the task continues uninterrupted.
 */
public class ProgressClient implements ReconnectionListener {
    // TODO: Implement text type of progress meter
    
    
    private boolean useNotification = false;
    
    public static final int TYPE_BAR = 0;
    public static final int TYPE_TEXT = 2;
    
    public static final int UPDATE_DELAY = 1000; // send every second
    
    private boolean progressChanged = false;
    private int nProgress = 0;
    private int nStallTimeoutSec = -1; // -1 indicates infinite
    private int nMax = 100;
    
    private int type = TYPE_BAR;
    private String name = "default";
    private final ArrayList<ClientInterface> clients = new ArrayList<ClientInterface>();
    
    private final Logger log = LogUtils.getLogger();
    
    public ProgressClient(Properties props) throws PropertiesException {
        
        // BEGIN PARSE PROPERTIES
        log.finest("Reading properties");
        String strUseNotification = props.getProperty("useNotification", "false");
        useNotification = strUseNotification.equals("true");
        
        if(useNotification) {
            String[] mandatoryValues = new String[] {
                    "notification.hosts",
                    "notification.name",
                    "notification.retryTimeoutSec",
                    "notification.stallTimeoutSec",
                    "notification.type"
                    };
            
            PropertyUtils.validateProperties(props, mandatoryValues);
            
            String[] hosts = props.getProperty("notification.hosts").split(" ");
            name = props.getProperty("notification.name");
            int nRetryTimeoutSec = Integer.parseInt(props.getProperty("notification.retryTimeoutSec"));
            nStallTimeoutSec = Integer.parseInt(props.getProperty("notification.stallTimeoutSec", "-1"));
            String strType = props.getProperty("notification.type", "bar");
            if(strType.equals("bar")) {
                type = TYPE_BAR;
            } else {
                type = TYPE_TEXT;
            }
            // END PARSE PROPERTIES
            
            for(String hostAndPort : hosts) {
                log.finest("Adding hostAndPort: " + hostAndPort);
	            String host = StringUtils.substringBefore(hostAndPort, ":");
	            int port = Integer.parseInt(StringUtils.substringAfter(hostAndPort, ":"));
	            LazyClient client = new LazyClient(host, port, nRetryTimeoutSec, log);
	            client.addReconnectionListener(this);
	            clients.add(client);
            }
            
            TimerTask task = new TimerTask() {
                public void run() {
                    sendProgress();
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, UPDATE_DELAY, UPDATE_DELAY);
        }
    }
    
    public void setProgress(int n) {
        synchronized(this) {
	        this.nProgress = n;
	        this.progressChanged = true;
        }
    }
    
    /**
     * This is called only internally at certain intervals to
     * reduce network traffic. Use setProgress for fast updates.
     */
    private void sendProgress() {
        synchronized(this) {
            if(progressChanged) {
		        sendMessageToAll("STAT " + name + " " + this.nProgress);
		        this.progressChanged = false;
            }
        }
    }
    
    /**
     * Set the maximum integer value of the progress bar
     * @param n The maximum value
     */
    public void setMaximumValue(int n) {
        this.nMax = n;
        sendMessageToAll("MAX " + name + " " + n);
    }
    
    /**
     * Announce that this task is done. 
     * 
     * @param success Was this task successful? If so,
     * the progress will be set to 100% and the bar will
     * turn green. If not, the bar will turn red and it
     * will stay at the same percentage.
     */
    public void setDone(boolean success) {
        this.progressChanged = false; // we don't want to update again
        sendMessageToAll("DONE " + name + " " + success);
    }
    
    /**
     * Tell the server how long to wait before declaring this
     * process stalled.
     * 
     * @param nSec How many seconds before a stall occurs
     */
    public void setStallTimeout(int nSec) {
        sendMessageToAll("STALL " + name + " " + nSec);
    }
    
    /**
     * Send a message to all clients
     * @param s The message to be sent
     */
    private void sendMessageToAll(String s) {
        for(ClientInterface client : clients) {
            try {
                client.sendMessage(s);
            } catch (ConnectionException e) {
                // this is a non-critical message
            }
        }
    }
    

    /*
     * A dropped connection was just reestablished. Bring that
     * client up to date on our current status.
     */
    public void connectionReestablished(ClientInterface client) {
        sendFullStatus(client);
    }
    
    /**
     * Send the full status of the task. This is usually done
     * for new connections or reconnections.
     */
    public void sendFullStatus(ClientInterface client) {
        try {
		    client.sendMessage("MAX " + name + " " + this.nMax);
		    client.sendMessage("STALL " + name + " " + this.nStallTimeoutSec);
		    client.sendMessage("STAT " + name + " " + this.nProgress);
        } catch(ConnectionException e) {
            // the lazy client should never let us get here
            e.printStackTrace();
        }
    }
    
    /*
     * For testing only
     */
    public static void main(String[] args) throws Exception {
	LogUtils.logAll();
	
        Properties props = PropertyUtils.getProperties("conf/notify.properties");
        ProgressClient cliente = new ProgressClient(props);
        props.setProperty("notification.name", "" + new Random().nextInt());
        
        // we need to send the name to the server

        cliente.setMaximumValue(600);
        for(int i=0; i<300; i++) {
            cliente.setProgress(i);
            Thread.sleep(10);
        }
        
        cliente.setStallTimeout(2);
        Thread.sleep(6000);
        
        for(int i=300; i<600; i++) {
            cliente.setProgress(i);
            Thread.sleep(10);
        }
        cliente.setDone(true);
    }
}
