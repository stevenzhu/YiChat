package com.shorigo.utils;

import java.util.Comparator;
import java.util.Map;

public class PinyinComparator implements Comparator<Map<String, String>> {

	public int compare(Map<String, String> o1, Map<String, String> o2) {
		if (o1.get("sort").equals("@") || o2.get("sort").equals("#")) {
			return -1;
		} else if (o1.get("sort").equals("#") || o2.get("sort").equals("@")) {
			return 1;
		} else {
			return o1.get("sort").compareTo(o2.get("sort"));
		}
	}

}
