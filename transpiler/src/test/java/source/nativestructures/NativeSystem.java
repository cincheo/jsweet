package source.nativestructures;

import static jsweet.util.Globals.$export;

import def.js.Array;

public class NativeSystem {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {

		long l1 = System.currentTimeMillis();

		trace.push("" + (l1 > 0));
		
		long l2 = System.currentTimeMillis();

		trace.push("" + (l2 >= l1));

		$export("trace", trace.join(","));

	}

}
