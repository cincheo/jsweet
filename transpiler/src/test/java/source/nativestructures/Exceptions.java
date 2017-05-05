package source.nativestructures;

import static jsweet.util.Lang.$export;

import def.js.Array;

public class Exceptions {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {
		try {
			new Exceptions().m();
		} catch (Exception e) {
			trace.push(e.getMessage());
		}
		try {
			new Exceptions().m();
		} catch (NoSuchMethodError e1) {
			trace.push(e1.getMessage());
		} catch (NumberFormatException e2) {
			trace.push(e2.getMessage());
		} catch (TestException3 e3) {
			trace.push(e3.getMessage());
		} finally {
			trace.push("finally");
		}
		try {
			new Exceptions().m2();
		} catch (Exception e) {
			trace.push(e.getMessage());
		}
		try {
			new Exceptions().m3();
		} catch (TestException e) {
			trace.push(e.getMessage());
		}
		$export("trace", trace.join(","));
	}

	public void m() throws NumberFormatException {
		throw new NumberFormatException("test");
	}

	public void m2() {
		throw new RuntimeException(new IllegalAccessError("test2"));
	}

	public void m3() throws TestException {
		throw new TestException("test3");
	}

}

class TestException extends Exception {

	public TestException(String msg) {
		super(msg);
	}

}

class TestException2 extends java.lang.Exception {

	public TestException2(String msg) {
		super(msg);
	}

}

class TestException3 extends java.lang.RuntimeException {

	public TestException3(String msg) {
		super(msg);
	}

}
