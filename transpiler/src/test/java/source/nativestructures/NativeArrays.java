package source.nativestructures;

import static jsweet.util.Globals.$export;

import def.js.Array;

public class NativeArrays {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {

		String[] a1 = { "a", "b", "c" };

		String[] a2 = new String[3];

		System.arraycopy(a1, 0, a2, 0, a1.length);

		trace.push("" + a2.length);
		trace.push(a2[0]);
		trace.push(a2[1]);
		trace.push(a2[2]);

		$export("trace", trace.join(","));

	}

}
