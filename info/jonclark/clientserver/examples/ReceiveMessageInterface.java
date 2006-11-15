/*
 * Created on Jun 12, 2006
 */
package info.jonclark.clientserver.examples;

import java.rmi.*;

public interface ReceiveMessageInterface extends Remote {
    void receiveMessage(String x) throws RemoteException;
}