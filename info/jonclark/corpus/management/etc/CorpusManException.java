/*
 * Created on Jun 15, 2007
 */
package info.jonclark.corpus.management.etc;

public class CorpusManException extends Exception {

    private static final long serialVersionUID = 3416633372246850544L;

    public CorpusManException(String message) {
	super(message);
    }

    public CorpusManException(String message, Throwable cause) {
	super(message, cause);
    }
}
