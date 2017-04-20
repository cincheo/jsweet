package source.nativestructures;

import static jsweet.util.Lang.$export;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import def.js.Array;

/**
 * This test is executed without any Java runtime.
 */
public class WeakReferences {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {

		WeakReference<String> ref = new WeakReference<String>("test");

		trace.push(ref.get());

		Map<String, String> map = new WeakHashMap<>();

		map.put("a", "1");

		trace.push(map.get("a"));

		$export("trace", trace.join(","));

	}

}
