package source.structural;

import static jsweet.util.Globals.$export;

import jsweet.lang.Array;

public class DefaultMethods<U> implements IDefaultMethods<U> {

	static Array<String> trace = new Array<String>();

	public static void main(String[] args) {
		new DefaultMethods<String>().m("");
		new DefaultMethods<String>().m2();
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

interface IDefaultMethods<T> {

	default void m(T t) {
		DefaultMethods.trace.push("m");
		m1();
	}

	default void m2() {
		DefaultMethods.trace.push("m2");
	}
	
	void m1();

}
