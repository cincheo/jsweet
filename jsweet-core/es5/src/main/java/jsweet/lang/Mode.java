package jsweet.lang;

/**
 * An enumeration to specify how JSweet behaves with regards to the language.
 */
public enum Mode {

	/**
	 * JSweet does not allow any Java to be used.
	 */
	STRICT,
	/**
	 * JSweet allows some Java to be uses so that some code can be shared with
	 * it.
	 */
	SHARED;

}
