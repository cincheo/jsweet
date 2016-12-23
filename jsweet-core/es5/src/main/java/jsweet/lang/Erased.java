package jsweet.lang;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation type is used on types that should be erased at generation
 * time (casts and constructor invocations are removed).
 * 
 * <p>
 * This is mainly used for Java type disambiguation when the API defines two
 * methods that have the same erasure. Most programmers will not have to use it
 * directly.
 * 
 * @author Renaud Pawlak
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD })
public @interface Erased {
}
