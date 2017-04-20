package source.overload;

import static jsweet.util.Lang.$export;

import java.util.function.BinaryOperator;

import def.js.Array;

public class WrongOverloadConstructorWithVarargs<T> {

	static Array<String> trace = new Array<>();

//	@SafeVarargs
//	public WrongOverloadConstructorWithVarargs(int defaultLevel, BinaryOperator<T>... precedence) {
//		this(null, defaultLevel, precedence);
//		trace.push("c2");
//	}

	@SafeVarargs
	private WrongOverloadConstructorWithVarargs(WrongOverloadConstructorWithVarargs<T> parent, int defaultLevel, BinaryOperator<T>... precedence) {
		trace.push("c1"+defaultLevel);
	}

//	@SafeVarargs
//	public final void test(int defaultLevel, BinaryOperator<T>... precedence) {
//		test(null, defaultLevel, precedence);
//		trace.push("t2");
//	}

	private void test(WrongOverloadConstructorWithVarargs<T> parent, int defaultLevel, @SuppressWarnings("unchecked") BinaryOperator<T>... precedence) {
		trace.push("t1"+defaultLevel);
	}

	public static void main(String[] args) {
//		new WrongOverloadConstructorWithVarargs<Double>().test(2);
		new WrongOverloadConstructorWithVarargs<Double>(null, 1, (a, b) -> a + b, (a, b) -> a - b).test(null, 2, (a, b) -> a + b, (a, b) -> a - b);
		// same but without varargs
		new WrongOverloadConstructorWithVarargs<Double>(null, 3).test(null, 4);
		$export("trace", trace);
	}

}
