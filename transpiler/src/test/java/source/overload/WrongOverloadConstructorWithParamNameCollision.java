package source.overload;

import static jsweet.util.Lang.$export;

import def.js.Array;

public class WrongOverloadConstructorWithParamNameCollision {

	static Array<String> trace = new Array<>();

	public WrongOverloadConstructorWithParamNameCollision(int min, int max, int value) {
		trace.push("" + min);
		trace.push("" + max);
		trace.push("" + value);
	}

	public WrongOverloadConstructorWithParamNameCollision(int orientation, int min, int max, int value) {
		trace.push("" + orientation);
		trace.push("" + min);
		trace.push("" + max);
		trace.push("" + value);
	}

	public static void main(String[] args) {
		new WrongOverloadConstructorWithParamNameCollision(1, 2, 3);
		new WrongOverloadConstructorWithParamNameCollision(4, 5, 6, 7);
		$export("trace", trace.join(","));
	}

}
