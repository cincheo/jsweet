package def.js;

/**
 * This class holds all the global functions and variables of the def.js
 * package.
 */
public final class Globals {
	private Globals() {
	}

	/**
	 * The constant representing a number that is "Not A Number".
	 */
	public static final double NaN = 0;
	
	/**
	 * The constant representing an infinite number.
	 */
	public static final double Infinity = 0;
	
	/**
	 * The JavaScript <code>undefined</code> global property.
	 */
	public static final java.lang.Object undefined = null;
	
	/**
	 * Accesses the current method arguments (<code>arguments</code> implicit
	 * variable in JavaScript). This variable is only available within
	 * functions.
	 */
	public static final def.js.Object[] arguments = null;

	/**
	 * Evaluates JavaScript code and executes it.
	 * 
	 * @param x
	 *            A String value that contains valid JavaScript code.
	 */
	native public static <T> T eval(java.lang.String x);

	/**
	 * Evaluates JavaScript code and executes it.
	 * 
	 * @param x
	 *            A String value that contains valid JavaScript code.
	 */
	native public static <T> T eval(String x);
	
	/**
	 * Converts A string to an integer.
	 * 
	 * @param s
	 *            A string to convert into a number.
	 * @param radix
	 *            A value between 2 and 36 that specifies the base of the number
	 *            in numString. If this argument is not supplied, strings with a
	 *            prefix of '0x' are considered hexadecimal. All other strings
	 *            are considered decimal.
	 */
	native public static double parseInt(java.lang.String s, double radix);

	/**
	 * Converts a string to a floating-point number.
	 * 
	 * @param string
	 *            A string that contains a floating-point number.
	 */
	native public static double parseFloat(java.lang.String string);

	/**
	 * Returns a Boolean value that indicates whether a value is the reserved
	 * value NaN (not a number).
	 * 
	 * @param number
	 *            A numeric value.
	 */
	native public static java.lang.Boolean isNaN(double number);

	/**
	 * Determines whether a supplied number is finite.
	 * 
	 * @param number
	 *            Any numeric value.
	 */
	native public static java.lang.Boolean isFinite(double number);

	/**
	 * Gets the unencoded version of an encoded Uniform Resource Identifier
	 * (URI).
	 * 
	 * @param encodedURI
	 *            A value representing an encoded URI.
	 */
	native public static java.lang.String decodeURI(java.lang.String encodedURI);

	/**
	 * Gets the unencoded version of an encoded Uniform Resource Identifier
	 * (URI).
	 * 
	 * @param encodedURI
	 *            A value representing an encoded URI.
	 */
	native public static String decodeURI(String encodedURI);
	
	/**
	 * Gets the unencoded version of an encoded component of a Uniform Resource
	 * Identifier (URI).
	 * 
	 * @param encodedURIComponent
	 *            A value representing an encoded URI component.
	 */
	native public static java.lang.String decodeURIComponent(java.lang.String encodedURIComponent);

	/**
	 * Gets the unencoded version of an encoded component of a Uniform Resource
	 * Identifier (URI).
	 * 
	 * @param encodedURIComponent
	 *            A value representing an encoded URI component.
	 */
	native public static String decodeURIComponent(String encodedURIComponent);
	
	/**
	 * Encodes a text string as a valid Uniform Resource Identifier (URI)
	 * 
	 * @param uri
	 *            A value representing an encoded URI.
	 */
	native public static java.lang.String encodeURI(java.lang.String uri);

	/**
	 * Encodes a text string as a valid Uniform Resource Identifier (URI)
	 * 
	 * @param uri
	 *            A value representing an encoded URI.
	 */
	native public static String encodeURI(String uri);
	
	/**
	 * Encodes a text string as a valid component of a Uniform Resource
	 * Identifier (URI).
	 * 
	 * @param uriComponent
	 *            A value representing an encoded URI component.
	 */
	native public static java.lang.String encodeURIComponent(java.lang.String uriComponent);

	/**
	 * Encodes a text string as a valid component of a Uniform Resource
	 * Identifier (URI).
	 * 
	 * @param uriComponent
	 *            A value representing an encoded URI component.
	 */
	native public static String encodeURIComponent(String uriComponent);

	public static ProxyConstructor Proxy;

	/**
	 * Converts A string to an integer.
	 * 
	 * @param s
	 *            A string to convert into a number.
	 * @param radix
	 *            A value between 2 and 36 that specifies the base of the number
	 *            in numString. If this argument is not supplied, strings with a
	 *            prefix of '0x' are considered hexadecimal. All other strings
	 *            are considered decimal.
	 */
	native public static int parseInt(java.lang.String s);
	
	/**
	 * Converts A string to an integer.
	 * 
	 * @param s
	 *            A string to convert into a number.
	 * @param radix
	 *            A value between 2 and 36 that specifies the base of the number
	 *            in numString. If this argument is not supplied, strings with a
	 *            prefix of '0x' are considered hexadecimal. All other strings
	 *            are considered decimal.
	 */
	native public static int parseInt(String s);

}
