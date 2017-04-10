package source.nativestructures;

import static jsweet.util.Globals.$export;

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

		StringBuffer sb2 = new StringBuffer();

		sb2.append("a");

		trace.push(sb2.toString());

		sb2.append("bc");

		trace.push("" + sb2);

		sb2.setLength(2);

		trace.push(sb2.toString());

		sb2.setLength(0);

		trace.push("X" + sb2.toString());

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
