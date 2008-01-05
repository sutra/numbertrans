package info.jonclark.util;

/**
 * Thrown by GetOpts to indicate improper usage of the command line.
 */
public class UsageException extends Exception {
    public UsageException(String message) {
	super(message);
    }
}
