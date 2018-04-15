package com.shorigo.utils;

import java.security.MessageDigest;

/**
 * @类名称: MD5Util
 * @类描述: MD5加密的工具类
 * @创建人：peidongxu
 */
public class MD5Util {

	private final String ALGORIGTHM_MD5 = "MD5";

	/*******************************
	 * 字符串生成MD5
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 *******************************/
	public String createMD5(String str) throws Exception {
		return createMD5(str, null);
	}

	/*******************************
	 * 字符串生成MD5
	 * 
	 * @param str
	 * @param charSet
	 *            编码格式
	 * @return
	 * @throws Exception
	 *******************************/
	public String createMD5(String str, String charSet) throws Exception {
		byte[] data;
		if (charSet != null && !"".equals(charSet)) {
			data = str.getBytes(charSet);
		} else {
			data = str.getBytes();
		}
		MessageDigest messageDigest = MessageDigest.getInstance(ALGORIGTHM_MD5);
		messageDigest.update(data);
		return byteArrayToHexString(messageDigest.digest());
	}

	/*******************************
	 * 字节数组转换为16进制字符串
	 * 
	 * @param data
	 * @return
	 *******************************/
	private String byteArrayToHexString(byte[] data) {
		// 用来将字节转换成 16 进制表示的字符
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		// 每个字节用 16 进制表示的话,使用两个字符,所以表示成 16 进制需要 32 个字符
		char arr[] = new char[16 * 2];
		int k = 0; // 表示转换结果中对应的字符位置
		// 从第一个字节开始,对 MD5 的每一个字节转换成 16 进制字符的转换
		for (int i = 0; i < 16; i++) {
			byte b = data[i]; // 取第 i 个字节
			// 取字节中高 4 位的数字转换, >>>为逻辑右移,将符号位一起右移
			arr[k++] = hexDigits[b >>> 4 & 0xf];
			// 取字节中低 4 位的数字转换
			arr[k++] = hexDigits[b & 0xf];
		}
		// 换后的结果转换为字符串
		return new String(arr);
	}

}
