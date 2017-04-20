package source.api;

import static jsweet.util.Lang.$export;

import def.js.Array;

public class Strings {

	static Array<String> trace = new Array<String>();

	public static void main(String[] args) {
		byte[] bytes = "abc".getBytes();
		char[] chars = "abc".toCharArray();
		char c = 'a';
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

		// TODO: fix it on Travis
		// trace.push(String.valueOf(str1.compareTo(str2)));
		trace.push(String.valueOf(str1.compareToIgnoreCase(str2)));
		trace.push(String.valueOf(str1.equals(str2)));
		trace.push(String.valueOf(str1.equalsIgnoreCase(str2)));

		trace.push(Strings.class.getName());
		trace.push(Strings.class.getSimpleName());

		trace.push("a b c".replaceAll(" ", ""));
		trace.push(String.valueOf("abcdabcdabcd".replace("ab", "")));

		trace.push(String.valueOf("abc".startsWith("ab")));
		trace.push(String.valueOf("abc".startsWith("c")));
		trace.push(String.valueOf("abc".endsWith("bc")));
		trace.push(String.valueOf("abc".endsWith("a")));

		trace.push(String.valueOf("abc".contains("a")));
		trace.push(String.valueOf("abc".contains("ab")));
		trace.push(String.valueOf("abc".contains("d")));
		trace.push(String.valueOf(c));
		trace.push("a"+c);
		
		$export("trace", trace.join(","));
	}

}
