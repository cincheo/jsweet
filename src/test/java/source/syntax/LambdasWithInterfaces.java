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
		new LambdasWithInterfaces().handler3(() -> {
			trace.push("ok3");
		});
		new LambdasWithInterfaces().handler4(() -> {
			trace.push("ok4");
		});
		$export("trace", trace.join(","));
	}

	public void handler(ANonFunctionalInterface2 i) {
		i.m();
	}

	public void handler3(AFunctionalInterface3 i) {
		i.apply();
	}

	public void handler4(AFunctionalInterface4 i) {
		i.m();
	}

}

interface ANonFunctionalInterface2 {
	void m();
}

interface AFunctionalInterface3 {
	void apply();
}

@FunctionalInterface
interface AFunctionalInterface4 {
	void m();
}
