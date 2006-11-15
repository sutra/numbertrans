/*
 * Created on Jun 12, 2006
 */
package info.jonclark.clientserver.examples;


import java.rmi.*;
import java.rmi.registry.*;
import java.net.*;

public class RmiClientExample
{
   static public void main(String args[])
   {
      ReceiveMessageInterface rmiServer;
      Registry registry;
      String serverAddress=args[0];
      String serverPort=args[1];
      String text=args[2];
      System.out.println("sending "+text+" to "+serverAddress+":"+serverPort);
      try{
          // TODO: Turn this into a static method in a generic class
          // so that you can get a server for any type of interface
          // without all this hubub
          
          
          // get the “registry”
          registry=LocateRegistry.getRegistry(
              serverAddress,
              Integer.parseInt(serverPort)
          );
          // look up the remote object
          rmiServer=
             (ReceiveMessageInterface)(registry.lookup("rmiServer"));
          // call the remote method
          rmiServer.receiveMessage(text);
      }
      catch(RemoteException e){
          e.printStackTrace();
      }
      catch(NotBoundException e){
          e.printStackTrace();
      }
   }
}