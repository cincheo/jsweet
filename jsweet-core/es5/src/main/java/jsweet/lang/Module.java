package jsweet.lang;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation tells that the target element is exported by an external
 * module.
 * 
 * <p>
 * This implies that when the Java program uses (import) that element, the given
 * module will be required if transpiling using modules. This is a way to invoke
 * "require" in the generated TypeScript/JavaScript code, but it remains
 * transparent for the JSweet programmer.
 * 
 * @author Renaud Pawlak
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PACKAGE, ElementType.METHOD, ElementType.FIELD })
public @interface Module {
	/**
	 * The name of the module to be required.
	 */
	java.lang.String value();
}
