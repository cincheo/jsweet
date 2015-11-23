package org.jsweet.test.transpiler.source.structural.globalclasses.root.a;

import static jsweet.util.Globals.$export;
import static org.jsweet.test.transpiler.source.structural.globalclasses.root.a.Globals.m2;

public class GlobalsInRoot {

	public void f1() {
		Globals.m1();
	}

	public void f2() {
		m2();
	}
}

class Globals {

	public static void m1() {
		$export("m1", true);
	}

	public static void m2() {
		$export("m2", true);
	}

	public static void main(String[] args) {
		GlobalsInRoot o = new GlobalsInRoot();
		o.f1();
		o.f2();
	}

}
