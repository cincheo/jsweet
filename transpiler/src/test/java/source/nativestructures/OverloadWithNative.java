package source.nativestructures;

import static jsweet.util.Lang.$export;

import java.util.Arrays;
import java.util.List;

import def.js.Array;

public class OverloadWithNative {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {
		new OverloadWithNative(Arrays.asList("a", "b"));
		new OverloadWithNative("a");

		$export("trace", trace.join(","));
	}
	
	public OverloadWithNative(List<String> l) {
		trace.push("1");
	}

	public OverloadWithNative(String s) {
		trace.push("2");
	}
	
}
