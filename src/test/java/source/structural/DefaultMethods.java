package source.structural;

import static jsweet.util.Globals.$export;

import jsweet.lang.Array;

public class DefaultMethods implements IDefaultMethods {

	static Array<String> trace = new Array<String>();

	public static void main(String[] args) {
		new DefaultMethods().m();
		new DefaultMethods().m2();
		$export("trace", trace.join());
	}

	@Override
	public void m1() {
		trace.push("m1");
	}

	@Override
	public void m2() {
		DefaultMethods.trace.push("m2-overriden");
	}
	
}

interface IDefaultMethods {

	default void m() {
		DefaultMethods.trace.push("m");
		m1();
	}

	default void m2() {
		DefaultMethods.trace.push("m2");
	}
	
	void m1();

}
