package jsweet.util.tuple;

/**
 * This class allows to simulate tuples of 6 elements, tuple types representing
 * arrays with individually tracked element types.
 * 
 * @author Renaud Pawlak
 *
 * @param <T0> the type of the 0-indexed element
 * @param <T1> the type of the 1-indexed element
 * @param <T2> the type of the 2-indexed element
 * @param <T3> the type of the 3-indexed element
 * @param <T4> the type of the 4-indexed element
 * @param <T5> the type of the 5-indexed element
 */
public class Tuple6<T0, T1, T2, T3, T4, T5> extends Tuple5<T0, T1, T2, T3, T4> {

	/**
	 * The 5-indexed element.
	 */
	public T5 $5;

	/**
	 * Creates a 6-element tuple.
	 * @param $0 the 0-indexed element
	 * @param $1 the 1-indexed element
	 * @param $2 the 2-indexed element
	 * @param $3 the 3-indexed element
	 * @param $4 the 4-indexed element
	 * @param $5 the 5-indexed element
	 */
	public Tuple6(T0 $0, T1 $1, T2 $2, T3 $3, T4 $4, T5 $5) {
		super($0, $1, $2, $3, $4);
		this.$5 = $5;
	}

}
