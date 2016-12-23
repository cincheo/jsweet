package jsweet.util.tuple;

/**
 * This class allows to simulate tuples of 4 elements, tuple types representing
 * arrays with individually tracked element types.
 * 
 * @author Renaud Pawlak
 *
 * @param <T0> the type of the 0-indexed element
 * @param <T1> the type of the 1-indexed element
 * @param <T2> the type of the 2-indexed element
 * @param <T3> the type of the 3-indexed element
 */
public class Tuple4<T0, T1, T2, T3> extends Tuple3<T0, T1, T2> {

	/**
	 * The 3-indexed element.
	 */
	public T3 $3;

	/**
	 * Creates a 4-element tuple.
	 * @param $0 the 0-indexed element
	 * @param $1 the 1-indexed element
	 * @param $2 the 2-indexed element
	 * @param $3 the 3-indexed element
	 */
	public Tuple4(T0 $0, T1 $1, T2 $2, T3 $3) {
		super($0, $1, $2);
		this.$3 = $3;
	}

}
