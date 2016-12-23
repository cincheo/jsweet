package jsweet.lang;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation type is used to declare compile-time mixins, which allow for
 * cross-candy interface merging.
 * 
 * <p>
 * When a mixin targets an interface in another candy, the JSweet transpiler
 * will merge the two interfaces and generate a new one that will precede the
 * two in the classpath.
 * 
 * <p>
 * This annotation is used for API definitions and most programmers will not
 * have to use it directly.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Mixin {
	/**
	 * The target class of this mixin, that it to say the class to which it will
	 * be merged at compile-time so that programmers can use the mixin members
	 * as well.
	 */
	Class<?> target();
}
