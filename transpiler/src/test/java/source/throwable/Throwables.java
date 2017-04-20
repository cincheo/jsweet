package source.throwable;

import static jsweet.util.Lang.$export;

public class Throwables {

	public static void main(String[] args) {

		try {
			m1();
		} catch (MyThrowable1 t) {
			$export("catch1", true);
		}
		try {
			m2();
		} catch (MyThrowable2 t) {
			$export("catch2", true);
			$export("message2", t.getMessage());
			try {
				throw new MyThrowable2("message2", t);
			} catch (MyThrowable2 t2) {
				$export("catch3", true);
				$export("message3", t2.getMessage());
				$export("cause", t2.getCause()==null);
			}
		}

	}

	static void m1() throws MyThrowable1 {
		throw new MyThrowable1();
	}

	static void m2() {
		throw new MyThrowable2("message");
	}

}

@SuppressWarnings("serial")
class MyThrowable1 extends Exception {

}

@SuppressWarnings("serial")
class MyThrowable2 extends RuntimeException {

	public MyThrowable2(String message) {
		super(message);
	}

	public MyThrowable2(String message, Throwable cause) {
		super(message, cause);
	}

}