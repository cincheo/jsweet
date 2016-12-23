package jsweet.util.tuple;

/**
 * This class allows to simulate tuples of 2 elements, tuple types representing
 * arrays with individually tracked element types.
 * 
 * @author Renaud Pawlak
 *
 * @param <T0> the type of the 0-indexed element
 * @param <T1> the type of the 1-indexed element
 */
public class Tuple2<T0, T1> {

	/**
	 * The 0-indexed element.
	 */
	public T0 $0;
	/**
	 * The 1-indexed element.
	 */
	public T1 $1;

	/**
	 * Creates a 2-element tuple.
	 * @param $0 the 0-indexed element
	 * @param $1 the 1-indexed element
	 */
	public Tuple2(T0 $0, T1 $1) {
		super();
		this.$0 = $0;
		this.$1 = $1;
	}

}
