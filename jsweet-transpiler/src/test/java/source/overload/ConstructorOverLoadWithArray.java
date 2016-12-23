package source.overload;

import static jsweet.util.Globals.$export;

import jsweet.lang.Array;

public class ConstructorOverLoadWithArray {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {
		new SomeObject();
		new SomeObject(new String[] { "any", "string", "array" });
		$export("trace", trace.join(","));
	}

}

class SomeObject {
	public SomeObject() {
		ConstructorOverLoadWithArray.trace.push("1");
	}

	public SomeObject(String[] stringArray) {
		ConstructorOverLoadWithArray.trace.push("2");
	}
}
