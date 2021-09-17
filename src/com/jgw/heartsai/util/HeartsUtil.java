package com.jgw.heartsai.util;

public class HeartsUtil {

	public static final String CRLF = System.getProperty("line.separator");

	public static void throwErr(String str) throws RuntimeException {
		throw new RuntimeException(str);
	}

}
