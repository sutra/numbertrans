/*
 * Created on Jun 15, 2007
 */
package info.jonclark.corpus.management.etc;

public class BadFilenameException extends Exception {

    private static final long serialVersionUID = 3416633372246850544L;

    public BadFilenameException(String message) {
	super(message);
    }
    
    public BadFilenameException(Throwable cause) {
	super(cause);
    }

    public BadFilenameException(String message, Throwable cause) {
	super(message, cause);
    }
}
