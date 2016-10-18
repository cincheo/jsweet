package source.syntax;

import static jsweet.util.Globals.$export;

import jsweet.lang.Array;

public class LambdasWithInterfaces {

	static Array<String> trace = new Array<String>();

	public static void main(String[] args) {
		new LambdasWithInterfaces().handler(new ANonFunctionalInterface2() {

			@Override
			public void m() {
				trace.push("ok1");
			}
		});
		new LambdasWithInterfaces().handler(() -> {
			trace.push("ok2");
		});
		$export("trace", trace.join(","));
	}

	public void handler(ANonFunctionalInterface2 i) {
		i.m();
	}

}

interface ANonFunctionalInterface2 {
	void m();
}
