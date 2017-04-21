package source.nativestructures;

import def.js.Array;
import jsweet.util.Lang;

public class Reflect {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {
		String className = "source.nativestructures.MyAccessedClass";

		try {
			Class<?> c = Class.forName(className);
			c.newInstance();
		} catch (Exception e) {
		}
		Lang.$export("trace", trace.join(","));
	}

}

class MyAccessedClass {

	public MyAccessedClass() {
		Reflect.trace.push("constructor");
	}

}
