/*
 * Created on Jun 12, 2006
 */
package info.jonclark.clientserver.examples;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.net.*;

// based on code from http://yama-linux.cc.kagoshima-u.ac.jp/~yamanoue/researches/java/rmi-ex2/
public class RmiServerExample extends UnicastRemoteObject
	implements ReceiveMessageInterface
{
    int thisPort;
    String thisAddress;
    Registry registry; // rmi registry for lookup the remote objects.
    // This method is called from the remote client by the RMI.
    // This is the implementation of the “ReceiveMessageInterface”.

    public void receiveMessage(String x) throws RemoteException
    {
        System.out.println(x);
    }

    public RmiServerExample() throws RemoteException
    {
        try {
            // get the address of this host.
            thisAddress = (InetAddress.getLocalHost()).toString();
        }

        catch (Exception e) {
            throw new RemoteException("can't get inet address.");
        }

        thisPort = 3232; // this port(registry’s port)
        System.out.println("this address=" + thisAddress + ",port=" + thisPort);

        try {
            // create the registry and bind the name and object.
            registry = LocateRegistry.createRegistry(thisPort);
            registry.rebind("rmiServer", this);
        }

        catch (RemoteException e) {
            throw e;
        }

    }

    static public void main(String args[])
    {
        try {
            RmiServerExample s = new RmiServerExample();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}