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
@Documented
public @interface Module {
	/**
	 * The name of the module to be required.
	 */
	java.lang.String value();
	
	/**
	 * The name of the exported element (module 'foo' { export = [exportedElement]; });
	 */
	java.lang.String exportedElement() default "";
}
