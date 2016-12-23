package jsweet.util.tuple;

/**
 * This class allows to simulate tuples of 5 elements, tuple types representing
 * arrays with individually tracked element types.
 * 
 * @author Renaud Pawlak
 *
 * @param <T0> the type of the 0-indexed element
 * @param <T1> the type of the 1-indexed element
 * @param <T2> the type of the 2-indexed element
 * @param <T3> the type of the 3-indexed element
 * @param <T4> the type of the 4-indexed element
 */
public class Tuple5<T0, T1, T2, T3, T4> extends Tuple4<T0, T1, T2, T3> {

	/**
	 * The 4-indexed element.
	 */
	public T4 $4;

	/**
	 * Creates a 5-element tuple.
	 * @param $0 the 0-indexed element
	 * @param $1 the 1-indexed element
	 * @param $2 the 2-indexed element
	 * @param $3 the 3-indexed element
	 * @param $4 the 4-indexed element
	 */
	public Tuple5(T0 $0, T1 $1, T2 $2, T3 $3, T4 $4) {
		super($0, $1, $2, $3);
		this.$4 = $4;
	}

}
