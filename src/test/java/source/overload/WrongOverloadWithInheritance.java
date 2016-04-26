package source.overload;

import static jsweet.util.Globals.$export;

import jsweet.lang.Array;
import jsweet.lang.Date;

public class WrongOverloadWithInheritance {

	public static Array<String> trace = new Array<String>();

	public static void main(String[] args) {
		new MyFinalClass<String>();
		MyFinalClass<String> o = new MyFinalClass<String>(99);
		o.overloaded("s1");
		o.overloaded(99, "s2");
		o.overloaded(true);
		o.overloaded(new Date());
		o.overloaded2();
		o.overloaded2(6);
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
	
	public MyAbstractClass() {
		
	}
	
	public abstract void overloaded2();

	public void overloaded2(int i) {
		WrongOverloadWithInheritance.trace.push("5-" + i);
	};
	
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

	public MyFinalClass() {
		WrongOverloadWithInheritance.trace.push("0-" + 88);
	}

	public MyFinalClass(int i) {
		WrongOverloadWithInheritance.trace.push("0-" + i);
	}
	
	public void overloaded2() {
		overloaded2(5);
	}
	
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
