package info.jonclark.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public class HashUtils {

	public static <K, V> void append(Map<K, ArrayList<V>> map, K key, V value) {
		ArrayList<V> list = map.get(key);
		if (list == null) {
			list = new ArrayList<V>();
			map.put(key, list);
		}
		list.add(value);
	}
	
	public static <K, V> void put(Map<K, HashSet<V>> map, K key, V value) {
		HashSet<V> list = map.get(key);
		if (list == null) {
			list = new HashSet<V>();
			map.put(key, list);
		}
		list.add(value);
	}

	public static <T> void increment(Map<T, Integer> map, T key) {
		Integer value = map.get(key);
		if (value == null)
			value = 0;
		map.put(key, value + 1);
	}

	public static <T> void decrement(Map<T, Integer> map, T key) {
		Integer value = map.get(key);
		if (value == null)
			value = 0;
		map.put(key, value - 1);
	}

	public static <T> void add(Map<T, Integer> map, T key, int amount) {
		Integer value = map.get(key);
		if (value == null)
			value = 0;
		map.put(key, value + amount);
	}

	public static <T> void subtract(Map<T, Integer> map, T key, int amount) {
		Integer value = map.get(key);
		if (value == null)
			value = 0;
		map.put(key, value - amount);
	}

	public static void main(String[] args) throws Exception {
		System.out.println(Integer.toString(' '));
	}
}
