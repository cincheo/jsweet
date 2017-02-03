/* 
 * JSweet - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jsweet.lang;

import java.lang.annotation.Documented;
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
@Documented
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
