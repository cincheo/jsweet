package source.syntax;

import static jsweet.util.Lang.$export;

import def.js.Array;

public class LambdasWithInterfaces {

	static Array<String> trace = new Array<String>();

	private final String result = "ok5";

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

		new LambdasWithInterfaces().test5();

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

	public void test5() {
		ClosureOverFieldInterface5 i = () -> trace.push(result);
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

interface ClosureOverFieldInterface5 {
	void m();
}