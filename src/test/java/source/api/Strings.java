package source.api;

import static jsweet.util.Globals.$export;

import jsweet.lang.Array;

public class Strings {

	static Array<String> trace = new Array<String>();

	public static void main(String[] args) {
		byte[] bytes = "abc".getBytes();
		char[] chars = "abc".toCharArray();
		String s1 = new String(bytes, 1, 1);
		trace.push(s1);
		trace.push("abc".substring(1, 3));
		trace.push("abc".substring(2));
		String s2 = new String(chars, 1, 2);
		trace.push(s2);
		trace.push(String.valueOf("abc".length()));
		trace.push(String.valueOf(!"abc".isEmpty()));
		trace.push(String.valueOf("abc".subSequence(0, 2)));
		trace.push(String.valueOf("x x".codePointAt(1)));
		trace.push(String.valueOf(chars, 1, 1));
		
		String str1 = "abc";
		String str2 = "ABC";

		trace.push(String.valueOf(str1.compareTo(str2)));
		trace.push(String.valueOf(str1.compareToIgnoreCase(str2)));
		trace.push(String.valueOf(str1.equals(str2)));
		trace.push(String.valueOf(str1.equalsIgnoreCase(str2)));
		
		$export("trace", trace.join(","));
	}

}
