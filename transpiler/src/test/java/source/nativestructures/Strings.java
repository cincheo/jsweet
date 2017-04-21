package source.nativestructures;

import static jsweet.util.Lang.$export;

import def.js.Array;

public class Strings {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {
		trace.push(">");
		trace.push("" + "a".indexOf(0x61));
		$export("trace", trace.join(","));
	}

}
