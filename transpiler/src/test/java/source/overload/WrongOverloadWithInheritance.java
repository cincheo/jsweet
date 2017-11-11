package source.overload;

import static jsweet.util.Lang.$export;

import def.js.Array;
import def.js.Date;

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
		o.overloaded3(100, new Date());
		new Test2().o1(null);
		new Test2().o2(null);
		$export("trace", trace.join(","));
	}

}

interface MyInterface<T> {
	void overloaded(T arg);

	void overloaded(boolean arg);

	void m1();

	void a();

	void a(int i);
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

	public void overloaded3() {
		WrongOverloadWithInheritance.trace.push("7");
	}

	@Override
	public void m1() {
		WrongOverloadWithInheritance.trace.push("m1");
	}

	@Override
	public void a() {
		a(0);
	}

	@Override
	public void a(int i) {
		WrongOverloadWithInheritance.trace.push("a");
	}
}

abstract class MyAbstractClass2<T> implements MyInterface2<T> {

	public MyAbstractClass2() {

	}

	@Override
	public void overloaded(boolean arg) {
		overloaded(1, null);
	}

	@Override
	public void overloaded(Date arg) {
		overloaded(2, null);
	}

	@Override
	public void overloaded(int i, T arg) {
	}

	@Override
	public void a() {
		a(0);
	}

	@Override
	public void a(int i) {
		WrongOverloadWithInheritance.trace.push("a");
	}

}

class MyAbstractClass3<T> extends MyAbstractClass<T> implements MyInterface2<T> {

	public MyAbstractClass3() {
		WrongOverloadWithInheritance.trace.push("0-" + 88);
	}

	public MyAbstractClass3(int i) {
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

	/**@Override // TODO results in infinite recursion
	public void overloaded(boolean arg) {
		WrongOverloadWithInheritance.trace.push("8-" + arg);
		super.overloaded(arg);
	}**/

	@Override
	public void overloaded3() {
		WrongOverloadWithInheritance.trace.push("6");
		super.overloaded3();
	}
}

class MyFinalClass<T> extends MyAbstractClass3<T> {

	public MyFinalClass() {
	}

	public MyFinalClass(int i) {
		super(i);
	}

	public void m3() {
		WrongOverloadWithInheritance.trace.push("m3");
	}

	public void overloaded3(int i, Date arg) {
		m3();
		super.overloaded3();
	}

}

class SubClass3 extends MyFinalClass<String> {

	@Override
	public void overloaded(int i, String arg) {
		m2();
	}

}

class Test1 {

	public void o1(Date date, double d) {
		WrongOverloadWithInheritance.trace.push("test1");
	}

	public void o2(Date date) {
		WrongOverloadWithInheritance.trace.push("test1");
	}

}

class Test2 extends Test1 {

	public void o1(Date date) {
		WrongOverloadWithInheritance.trace.push("test2");
	}

	public void o2(Date date, double d) {
		WrongOverloadWithInheritance.trace.push("test2");
	}

}

class Point {
	public int x;
	public int y;
}

interface IRoot {

	void mRoot(int i, int j);

	void contains(Point p);

	void contains(int x, int y);

}

abstract class AbstractRoot implements IRoot {

	public void mRoot(int i) {
		WrongOverloadWithInheritance.trace.push("m");
	}

	@Override
	public void contains(Point p) {
		contains(p.x, p.y);
	}

}
