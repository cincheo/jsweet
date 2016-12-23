package jsweet.util.function;

/**
 * Represents an operation that accepts 4 input arguments and returns no
 * result.
 */
@FunctionalInterface
public interface Consumer4<T1, T2, T3, T4> {

	/**
	 * Performs this operation on the given arguments.
	 */
	void apply(T1 p1, T2 p2, T3 p3, T4 p4);

}
