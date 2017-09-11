package source.nativestructures;

import static jsweet.util.Lang.$export;

import def.js.Array;

public class Strings {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {
		trace.push(">");
		trace.push("" + "a".indexOf(0x61));
		byte[] bytes = { 65, 66, 67 };
		char[] chars = { 'a', 'b', 'c' };
		trace.push(new String(bytes));
		trace.push(new String(chars));
		StringBuffer sb = new StringBuffer();
		sb.append("abcd");
		trace.push(new String(sb));
		trace.push(new String(bytes, 0, 2));
		trace.push(new String(chars, 1, 1));
		$export("trace", trace.join(","));
		assert "" == new String();
		byte[] bytes2 = { 'a', 'b', 'c' };
		// this one is not really supported and may produce unexpected results at runtime
		String s = new String(bytes2, 0);
	}

}
