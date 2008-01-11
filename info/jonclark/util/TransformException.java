package info.jonclark.util;

public class TransformException extends Exception {
	private static final long serialVersionUID = -3784001595327375161L;

	public TransformException(String message, Throwable cause) {
		super(message, cause);
	}

	public TransformException(String message) {
		super(message);
	}

	public TransformException(Throwable cause) {
		super(cause);
	}

}
