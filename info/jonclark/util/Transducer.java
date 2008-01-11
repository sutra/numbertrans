package info.jonclark.util;

public interface Transducer {
	public String transform(String in) throws TransformException;
}
