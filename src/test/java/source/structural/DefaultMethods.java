package source.structural;

import static jsweet.util.Globals.$export;

import jsweet.lang.Array;

public class DefaultMethods implements IDefaultMethods {

	static Array<String> trace = new Array<String>();

	public static void main(String[] args) {
		new DefaultMethods().m();
		$export("trace", trace.join());
	}

	@Override
	public void m1() {
		trace.push("m1");
	}

}

interface IDefaultMethods {

	default void m() {
		DefaultMethods.trace.push("m");
		m1();
	}

	void m1();

}
