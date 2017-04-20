package source.overload;

import static jsweet.util.Lang.$export;

import def.js.Array;

public class ConstructorOverLoadWithArray {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {
		new SomeObject();
		SomeObject so = new SomeObject(new String[] { "any", "string", "array" });
		so.m(new int[] { 1, 2 });
		so.m(new String[] { "1", "2" });
		so.m(new int[2]);

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

	void m(String[] s) {
		ConstructorOverLoadWithArray.trace.push("4");
	}

	void m(int[] s) {
		ConstructorOverLoadWithArray.trace.push("3");
	}
	
}
