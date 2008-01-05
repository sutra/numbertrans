/*
 * Created on Nov 27, 2006
 */
package info.jonclark.lang;

public class Pair<T, V> {
	public T first;
	public V second;

	public Pair() {
	}

	public Pair(T first, V second) {
		this.first = first;
		this.second = second;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Pair) {
			Pair other = (Pair) obj;
			return (this.first.equals(other.first) && this.second.equals(other.second));
		} else {
			return false;
		}
	}

	public String toString() {
		return "[" + first.toString() + ", " + second.toString() + "]";
	}
}
