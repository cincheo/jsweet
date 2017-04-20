package source.overload;

import static jsweet.util.Lang.$export;
import static jsweet.util.Lang.array;

public class WrongOverloadWithGenerics {
	public static String[] trace = {};

	public static void main(String[] args) {
		new WithGenerics<String, Integer>().m("test");
		new WithGenerics<String, Integer>().m("test", 2);
		new WithGenerics<String, Integer>().m("test", 2, 2);
		new WithGenerics<String, Integer>().m("test", "test");
		$export("trace", trace);
	}

}

class WithGenerics<T1, T2 extends Number> {

	void m(T1 t) {
		array(WrongOverloadWithGenerics.trace).push("1");
	}

	void m(T1 t, int i) {
		array(WrongOverloadWithGenerics.trace).push("2");
	}

	void m(T1 t, int i, T2 t2) {
		array(WrongOverloadWithGenerics.trace).push("3");
	}

	<U> void m(T1 t, U u) {
		array(WrongOverloadWithGenerics.trace).push("4");
	}

}
