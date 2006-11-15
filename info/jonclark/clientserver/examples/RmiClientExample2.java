/*
 * Created on Jun 12, 2006
 */
package info.jonclark.clientserver.examples;

import info.jonclark.clientserver.RmiClient;

public class RmiClientExample2
{
   static public void main(String args[]) throws Exception
   {
      String host=args[0];
      String port=args[1];
      String text=args[2];
      
      final ReceiveMessageInterface rmiServer =
          (ReceiveMessageInterface) RmiClient.getRemoteObject(host, port, "rmiServer");
      rmiServer.receiveMessage(text);
   }
}