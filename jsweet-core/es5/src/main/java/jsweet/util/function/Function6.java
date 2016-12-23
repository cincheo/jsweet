package jsweet.util.function;

/**
 * Represents a function that accepts 5 arguments and produces a result.
 */
public interface Function6<T1, T2, T3, T4, T5, T6, R> {

	/**
	 * Applies this function to the given arguments.
	 */
	R apply(T1 p1, T2 p2, T3 p3, T4 p4, T5 p5, T6 p6);

}
