/*
 * Created on Jun 17, 2006
 */
package info.jonclark.clientserver;

/**
 * @author Jonathan
 */
public interface ReconnectionListener {
    /**
     * Connection has just been reestablished. We should
     * probably sent the client our most recent state information.
     * @param client
     */
    public void connectionReestablished(ClientInterface client);
}
