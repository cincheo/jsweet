package source.nativestructures;

import static jsweet.util.Lang.$export;

import def.js.Array;

/**
 * This test is executed without any Java runtime.
 */
public class NativeStringBuilder {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder();

		sb.append("a");

		trace.push(sb.toString());

		sb.append("bc");

		trace.push("" + sb);

		trace.push(sb.lastIndexOf("c") + "");

		StringBuffer sb2 = new StringBuffer();

		sb2.append("a");

		trace.push(sb2.toString());

		sb2.append("bc");

		trace.push("" + sb2);

		sb2.setLength(2);

		trace.push(sb2.toString());

		sb2.setLength(0);

		trace.push("X" + sb2.toString());

		StringBuffer sb3 = new StringBuffer("test");

		sb3.setCharAt(1, 'E');
		
		trace.push(sb3.toString());
		
		trace.push(""+sb3.charAt(1));
		
		trace.push(""+sb3.length());

		sb3.deleteCharAt(1);

		trace.push(""+sb3);

		sb3.delete(1, 2);

		trace.push(""+sb3);
		
		StringBuilder result = new StringBuilder();
		final String variableName = "qqqq";
		result.append(":").append(variableName);
		trace.push(result.toString());

		result.insert(1, "aaaa");
		trace.push(result.toString());

		trace.push(result.substring(3));
		trace.push(result.substring(3, 7));

		$export("trace", trace.join(","));

	}

	StringBuilder sb2 = new StringBuilder();

	public void test(char[] c, int i, int l) {
		sb2.append(c);
		sb2.append(c, i, l);
	}

	public void overload(StringBuilder sb1) {
		
	}

	public void overload(StringBuilder sb2, String s) {
		
	}
	
	
}
