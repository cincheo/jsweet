package org.jsweet.test.transpiler.source.structural.globalclasses.noroot.a;

import static jsweet.util.Globals.$export;
import static org.jsweet.test.transpiler.source.structural.globalclasses.noroot.a.Globals.m2;

public class GlobalsInNoRoot {

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
		GlobalsInNoRoot o = new GlobalsInNoRoot();
		o.f1();
		o.f2();
	}

}
