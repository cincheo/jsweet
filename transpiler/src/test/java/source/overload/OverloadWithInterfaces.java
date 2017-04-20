package source.overload;

import static jsweet.util.Lang.$export;

import def.js.Array;

interface MyInterface1 {

}

class MyClass1 implements MyInterface1 {

}

public class OverloadWithInterfaces {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {
		m(new short[] { 1, 2 });
		m(new short[] { 1, 2 }, 0, 1);
		m(new short[] { 1, 2 }, new MyInterface1() {
		});
		m(new short[] { 1, 2 }, new MyClass1() {
		});
		$export("trace", trace.join(","));
	}

	public static void m(short[] array) {
		trace.push("1");
	}

	public static void m(short[] array, int fromIndex, int toIndex) {
		trace.push("2");
	}

	public static void m(short[] array, MyInterface1 i1) {
		trace.push("3");
	}

}
