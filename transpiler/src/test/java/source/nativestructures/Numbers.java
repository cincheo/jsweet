package source.nativestructures;

import static jsweet.util.Lang.$export;

import def.js.Array;

public class Numbers {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {
		trace.push(">");
		boolean knowPages = true;
		int pageMax = 1;
		trace.push(Integer.toString(knowPages ? pageMax : 0));
		$export("trace", trace.join(","));
		Double.valueOf("1.2");
	}

}
