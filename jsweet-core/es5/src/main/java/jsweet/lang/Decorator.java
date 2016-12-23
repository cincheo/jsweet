package jsweet.lang;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This meta-annotation is used to mark annotations as decorators.
 * 
 * <p>
 * On contrary to plain Java annotations, decorators will be generated as
 * TypeScript decorators. For example, the following defines a Component
 * decorator which can be used as a TypeScript decorator.
 * 
 * <pre>
 * &#64;Decorator
 * public @interface Component {
 * 	public String name;
 * }
 * </pre>
 * 
 * Using this annotation:
 * 
 * <pre>
 * &#64;Component(name="Hello")
 * public class C {
 * }
 * </pre>
 * 
 * Will generate the following TypeScript code:
 * 
 * <pre>
 * &#64;Component({name:"Hello"})
 * public class C {
 * }
 * </pre>
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE })
public @interface Decorator {
}
