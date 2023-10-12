package ts.utils;

public class BooleanUtils {

	/**
	 * <p>
	 * Converts a Boolean to a boolean handling {@code null} by returning
	 * {@code false}.
	 * </p>
	 *
	 * <pre>
	 *   BooleanUtils.toBoolean(Boolean.TRUE)  = true
	 *   BooleanUtils.toBoolean(Boolean.FALSE) = false
	 *   BooleanUtils.toBoolean(null)          = false
	 * </pre>
	 *
	 * @param bool
	 *            the boolean to convert
	 * @return {@code true} or {@code false}, {@code null} returns {@code false}
	 */
	public static boolean toBoolean(final Boolean bool) {
		return bool != null && bool.booleanValue();
	}
}
