/*
 * Created on Jun 17, 2006
 */
package info.jonclark.clientserver;

/**
 * @author Jonathan
 */
public class ConnectionException extends Exception {
    public ConnectionException(String message) {
        super(message);
    }
    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
