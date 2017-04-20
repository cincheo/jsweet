package source.nativestructures;

import static jsweet.util.Lang.$export;

import java.util.Arrays;

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

		trace.push("" + Arrays.equals(a1, a2));
		trace.push("" + Arrays.equals(a1, new String[] { "a", "b", "d" }));
		trace.push("" + Arrays.equals(a1, new String[] { "a", "b" }));
		trace.push("" + Arrays.equals(a1, new Integer[] { 1, 2, 3 }));

		trace.push("" + Arrays.equals(a1, a2.clone()));
		
		
		$export("trace", trace.join(","));

	}

}
