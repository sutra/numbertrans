/*
 * Created on Jun 12, 2006
 */
package info.jonclark.clientserver;

import java.rmi.*;
import java.rmi.registry.*;

public class RmiClient
{
   static public Remote getRemoteObject(final String host, final String port, final String bindName)
   	throws NumberFormatException, RemoteException, NotBoundException
   	{          
       final Registry registry=LocateRegistry.getRegistry(host, Integer.parseInt(port));
       return registry.lookup(bindName);
   }
}