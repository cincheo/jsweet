package source.overload;

import static jsweet.util.Globals.$export;

import jsweet.lang.Array;
import jsweet.lang.Date;

public class WrongOverloadWithInheritance {

	public static Array<String> trace = new Array<String>();

	public static void main(String[] args) {
		MyFinalClass<String> o = new MyFinalClass<String>();
		o.overloaded("s1");
		o.overloaded(99, "s2");
		o.overloaded(true);
		o.overloaded(new Date());
		$export("trace", trace.join(","));
	}

}

interface MyInterface<T> {
	void overloaded(T arg);

	void overloaded(boolean arg);

	void m1();
}

interface MyInterface2<T> extends MyInterface<T> {
	void overloaded(int i, T arg);

	void overloaded(Date arg);
}

abstract class MyAbstractClass<T> implements MyInterface<T> {
	public void overloaded(T arg) {
		WrongOverloadWithInheritance.trace.push("1-" + arg);
	};

	@Override
	public void overloaded(boolean arg) {
		WrongOverloadWithInheritance.trace.push("3-" + arg);
	}

	@Override
	public void m1() {
		WrongOverloadWithInheritance.trace.push("m1");
	}

}

class MyFinalClass<T> extends MyAbstractClass<T> implements MyInterface2<T> {
	@Override
	public void overloaded(int i, T arg) {
		m2();
		WrongOverloadWithInheritance.trace.push("2-" + i + "-" + arg);
	}

	public void m2() {
		WrongOverloadWithInheritance.trace.push("m2");
	}

	@Override
	public void overloaded(Date arg) {
		m1();
		WrongOverloadWithInheritance.trace.push("4");
	}
}
