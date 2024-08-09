package com.increff.posapp.util;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	private StringUtil() {}

	public static boolean isEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}

	public static String toLowerCase(String s) {
		return s == null ? null : s.trim().toLowerCase();
	}

	public static Boolean isNotAlNum(String s){
		String regex = "^[a-zA-Z0-9\\s-]*$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(s);
		return !matcher.matches();
	}

}
