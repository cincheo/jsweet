package source.structural.globalclasses.a;

import static jsweet.util.Lang.$export;

public interface InterfaceWithStaticMethod {

	static void aStaticMethod() {
		$export("m", true);
	}

	static void overloadedStaticMethod(String s) {
		$export("s", s);
	}

	static void overloadedStaticMethod(int i) {
		$export("i", i);
	}

}
