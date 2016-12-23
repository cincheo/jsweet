package jsweet.lang;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import def.js.Object;
import def.js.String;

/**
 * This annotation is used to specify external
 * classes/interfaces/functions/variables (typically from an external JavaScript
 * library) in order to use them in a a JSweet program. It won't be transpiled
 * but calls on this class/interface will be preserved.
 * 
 * <p>
 * All of the ambient class methods must be native.
 * 
 * <p>
 * For example, assume that you want to access a JavaScript library that defines
 * a <code>Request</code> class with a <code>url</code> field defined as a
 * <code>String</code>. You can then define the following ambient declaration.
 * 
 * <pre>
 * &#64;Ambient
 * public class Request {
 * 	public String url;
 * }
 * </pre>
 * 
 * <p>
 * You can then use this ambient type to access the <code>url</code> field in a
 * typed way.
 * 
 * <pre>
 * String url = ((Request) myUntypedObject).url;
 * </pre>
 * 
 * <p>
 * This solution is cleaner than using the {@link Object#$get(String)} function,
 * which is not typed.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
public @interface Ambient {

}
