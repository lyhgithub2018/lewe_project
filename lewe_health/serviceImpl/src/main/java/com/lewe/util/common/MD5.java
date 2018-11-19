package com.lewe.util.common;

import java.security.MessageDigest;

/**
 * 说明：MD5加密
 */
public class MD5 {

	/**
	 * 直接对原字符串进行md5加密
	 * @param str
	 * @return
	 */
	public static String md5(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes("utf-8"));
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			str = buf.toString();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return str;
	}
	
	/**
	 * 指定一个固定key值对原字符串进行md5加密
	 * @param str
	 * @param key
	 * @return
	 */
	public static String md5WithKey(String str,String key) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			if(key!=null) {
				str = str+key;
			}
			md.update(str.getBytes("utf-8"));
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			str = buf.toString();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return str;
	}
	
}
