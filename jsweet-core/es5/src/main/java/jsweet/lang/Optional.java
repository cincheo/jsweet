package jsweet.lang;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation shall be used on optional interface fields so that the transpiler does not complain when the field is not assigned.
 * 
 * <p>For example:
 * 
 * <pre>
 * @Interface
 * public class Data {
 *    String field1;
 *    @Optional
 *    String field2;
 * }
 * </pre>
 * 
 * <p>Allows for: <code>new Data() {{field1="value";}}</code>.
 * 
 * <p>On the contrary, the code above will not be transpiled if the @Optional annotation is omitted.
 * 
 * @author Renaud Pawlak
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
public @interface Optional {

}
