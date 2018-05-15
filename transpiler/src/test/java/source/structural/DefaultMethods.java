package source.structural;

import static source.structural.globalclasses.a.ClassWithStaticMethod.aStaticMethod;

import static jsweet.util.Lang.$export;

import def.js.Array;
import source.structural.globalclasses.a.ClassWithStaticMethod;

public class DefaultMethods<U> implements IDefaultMethods<U> {

	static Array<String> trace = new Array<String>();

	public static void main(String[] args) {
		new DefaultMethods<String>().m("");
		new DefaultMethods<String>().m2();
		new DefaultMethods<String>().overload(5);
		new DefaultMethods<String>().overload(15, "kako");
		$export("trace", trace.join(","));
	}

	@Override
	public void m1() {
		trace.push("m1");
	}

	@Override
	public void m2() {
		DefaultMethods.trace.push("m2-overriden");
	}

	@Override
	public boolean overload(int index, String p2) {
		trace.push("overload_called" + index + ";" + p2);
		return false;
	}

	@Override
	public Array<String> getTrace() {
		return trace;
	}

}

class DefaultMethodsFromAbstract<U> extends AbstractDefaultMethods<U> {

	static Array<String> trace = new Array<String>();

	public static void main(String[] args) {
		new DefaultMethodsFromAbstract<String>().m("");
		new DefaultMethodsFromAbstract<String>().m2();
		new DefaultMethodsFromAbstract<String>().overload(5);
		new DefaultMethodsFromAbstract<String>().overload(15, "kako");
		$export("FromAbstract_trace", trace.join(","));
	}

	@Override
	public void m1() {
		trace.push("m1");
	}

	@Override
	public void m2() {
		DefaultMethodsFromAbstract.trace.push("m2-overriden");
	}
	@Override
	public Array<String> getTrace() {
		return trace;
	}
	
	@Override
	public boolean overload(int index, String p2) {
		getTrace().push("FromAbstract_overload_called" + index + ";" + p2);
		return false;
	}
}
abstract class AbstractDefaultMethods<T> implements IDefaultMethods<T> {
	
}

interface IDefaultMethods<T> {

	default void m(T t) {
		getTrace().push("m");
		aStaticMethod();
		m1();
	}

	default void m2() {
		getTrace().push("m2");
		ClassWithStaticMethod.aStaticMethod();
	}

	void m1();

	default boolean overload(int index) {
		return overload(index, "p2");
	}

	boolean overload(int index, String p2);

	Array<String> getTrace();
}
