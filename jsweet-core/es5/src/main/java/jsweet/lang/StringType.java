package jsweet.lang;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to annotate fields that define string types (see
 * TypeScript string types).
 * 
 * @author Renaud Pawlak
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface StringType {

	/**
	 * Defines the actual value of the string type, to be used in case the
	 * string is not a valid Java identifier.
	 */
	java.lang.String value() default "";

}
