package source.nativestructures;

import static jsweet.util.Globals.$export;

import java.lang.ref.WeakReference;

import def.js.Array;

/**
 * This test is executed without any Java runtime.
 */
public class WeakReferences {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {

		WeakReference<String> ref = new WeakReference<String>("test");

		trace.push(ref.get());

		$export("trace", trace.join(","));

	}

}
