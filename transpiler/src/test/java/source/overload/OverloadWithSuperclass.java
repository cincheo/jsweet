package source.overload;

public class OverloadWithSuperclass {

	public static void main(String[] args) {
		m("a", new C1());
		m2("a", new MyException());
	}

	static void m(String s) {

	}

	static void m(String s, C0 c) {

	}

	static void m2(String s) {

	}

	static void m2(String s, Throwable e) {

	}

}

class C0 {

}

class C1 extends C0 {

}

class MyException extends Throwable {

}
