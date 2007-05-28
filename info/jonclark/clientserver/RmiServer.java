/*
 * Created on Jun 12, 2006
 */
package info.jonclark.clientserver;

import info.jonclark.clientserver.examples.ReceiveMessageInterface;
import info.jonclark.log.LogUtils;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.logging.Logger;
import java.net.*;

// based on code from http://yama-linux.cc.kagoshima-u.ac.jp/~yamanoue/researches/java/rmi-ex2/
public class RmiServer extends UnicastRemoteObject
{
    final int thisPort;
    final String thisAddress;
    final Registry registry; // rmi registry for lookup the remote objects.
    final Logger log = LogUtils.getLogger();
    // This method is called from the remote client by the RMI.
    // This is the implementation of the �ReceiveMessageInterface�.

    public RmiServer(String port, String bindName)
    	throws RemoteException
    {
        
        try {
            // get the address of this host.
            thisAddress = (InetAddress.getLocalHost()).toString();
        } catch (Exception e) {
            throw new RemoteException("Couldn't obtain server inetaddress", e);
        }

        thisPort = Integer.parseInt(port); // this port(registry�s port)
        log.finer("Starting RMI Server with address=" + thisAddress + ",port=" + thisPort);

        // create the registry and bind the name and object.
        registry = LocateRegistry.createRegistry(thisPort);
        registry.rebind(bindName, this);
        log.finer("Bound RMI server to name: " + bindName);
    }

    static public void main(String args[]) throws Exception
    {
            RmiServer s = new RmiServer("4242", "rmiServer");
    }

}