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
@Documented
public @interface Decorator {
}
