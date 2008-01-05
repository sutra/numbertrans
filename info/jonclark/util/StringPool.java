package info.jonclark.util;

import java.util.HashMap;

public class StringPool {
	
	private int serialNum = Integer.MIN_VALUE;
	private final HashMap<String, Integer> map = new HashMap<String, Integer>();
	private final HashMap<Integer, String> unmap = new HashMap<Integer, String>();
	
	public int map(String str) {
		Integer n = map.get(str);
		if(n == null) {
			if(n == Integer.MAX_VALUE) {
				throw new RuntimeException("Pool overflow.");
			}
			
			n = serialNum;
			serialNum++;
			map.put(str, n);
			unmap.put(n, str);
		}
		return n;
	}
	
	public String unmap(int n) {
		return unmap.get(n);
	}
}
