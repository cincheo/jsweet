package jsweet.lang;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation type is used on classes that will to declare that they are
 * not actual classes but interfaces.
 * 
 * <p>
 * In JSweet, most of the time, the API definitions use classes instead of
 * interfaces by choice, in order to be able to define properties with fields.
 * This way the syntax and use of the API is simpler and closer to the targeted
 * code.
 * 
 * @see Extends
 * @author Renaud Pawlak
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Interface {
	Mode value() default Mode.STRICT;
}
