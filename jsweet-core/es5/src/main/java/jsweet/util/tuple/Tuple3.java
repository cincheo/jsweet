package jsweet.util.tuple;

/**
 * This class allows to simulate tuples of 3 elements, tuple types representing
 * arrays with individually tracked element types.
 * 
 * @author Renaud Pawlak
 *
 * @param <T0> the type of the 0-indexed element
 * @param <T1> the type of the 1-indexed element
 * @param <T2> the type of the 2-indexed element
 */
public class Tuple3<T0, T1, T2> extends Tuple2<T0, T1> {

	/**
	 * The 2-indexed element.
	 */
	public T2 $2;

	/**
	 * Creates a 3-element tuple.
	 * @param $0 the 0-indexed element
	 * @param $1 the 1-indexed element
	 * @param $2 the 2-indexed element
	 */
	public Tuple3(T0 $0, T1 $1, T2 $2) {
		super($0, $1);
		this.$2 = $2;
	}
	
}
