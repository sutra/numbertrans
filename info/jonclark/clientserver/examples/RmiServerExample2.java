/*
 * Created on Jun 12, 2006
 */
package info.jonclark.clientserver.examples;

import info.jonclark.clientserver.RmiServer;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.logging.Logger;
import java.net.*;

// based on code from http://yama-linux.cc.kagoshima-u.ac.jp/~yamanoue/researches/java/rmi-ex2/
public class RmiServerExample2 extends RmiServer implements ReceiveMessageInterface
{

    public void receiveMessage(String x) throws RemoteException
    {
        System.out.println(x);
    }

    public RmiServerExample2(String host, String port) throws RemoteException
    {
        super(host, port);
    }

    static public void main(String args[]) throws Exception
    {
        System.err.println("Usage: Program <host> <port>");
        RmiServerExample2 s = new RmiServerExample2(args[0], args[1]);
        System.out.println("Server running...");
    }

}