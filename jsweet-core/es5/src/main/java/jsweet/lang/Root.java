package jsweet.lang;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This package annotation is used to specify a root package for the transpiled
 * TypeScript/JavaScript, which means that all transpiled references in this
 * package and subpackages will be relative to this root package.
 * 
 * <p>
 * As an example, given the <code>org.mycompany.mylibrary</code> root package
 * (annotated with @Root), the class
 * <code>org.mycompany.mylibrary.MyClass</code> will actually correspond to
 * <code>MyClass</code> in the JavaScript runtime. Similarly, the
 * <code>org.mycompany.mylibrary.mypackage.MyClass</code> will transpile to
 * <code>mypackage.MyClass</code>.
 * 
 * @author Renaud Pawlak
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PACKAGE })
public @interface Root {
	/**
	 * Declares the dependencies of this package if any. This is purely
	 * informational and is not used by the transpiler.
	 */
	public abstract java.lang.String[] dependencies() default {};

	/**
	 * Declares the classes in this root package and its subpackages that must
	 * be processed as mixins by the JSweet transpiler. Theses classes must be
	 * annotated with the {@link Mixin} annotation, which defines the target of
	 * the mixin class.
	 */
	public abstract java.lang.Class<?>[] mixins() default {};
}
