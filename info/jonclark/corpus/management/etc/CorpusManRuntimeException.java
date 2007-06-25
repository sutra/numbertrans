/*
 * Created on Jun 15, 2007
 */
package info.jonclark.corpus.management.etc;

public class CorpusManRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 3416633372246850544L;

    public CorpusManRuntimeException(String message) {
	super(message);
    }

    public CorpusManRuntimeException(String message, Throwable cause) {
	super(message, cause);
    }
}
