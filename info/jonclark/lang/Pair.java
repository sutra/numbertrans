/*
 * Created on Nov 27, 2006
 */
package info.jonclark.lang;

public class Pair<T,V> {
    public T first;
    public V second;
    
    public Pair() {}
    
    public Pair(T first, V second) {
	this.first = first;
	this.second = second;
    }
}
