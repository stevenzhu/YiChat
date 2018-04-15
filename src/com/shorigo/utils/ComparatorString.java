package com.shorigo.utils;

import java.util.Comparator;

/**
 * 字符串排序
 * 
 * @author peidongxu
 * 
 */
public class ComparatorString implements Comparator<String> {

	@Override
	public int compare(String lhs, String rhs) {
		int flg = lhs.compareTo(rhs);
		return flg;

	}

}
