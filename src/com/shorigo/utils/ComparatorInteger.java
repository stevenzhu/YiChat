package com.shorigo.utils;

import java.util.Comparator;

/**
 * 字符串排序
 * 
 * @author peidongxu
 * 
 */
public class ComparatorInteger implements Comparator<String> {

	@Override
	public int compare(String lhs, String rhs) {
		int tag = 0;
		Integer pre = Integer.valueOf(lhs);
		Integer next = Integer.valueOf(rhs);
		if (pre < next) {
			tag = -1;
		}
		if (pre == next) {
			tag = 0;
		}
		if (pre > next) {
			tag = 1;
		}
		return tag;

	}

}
