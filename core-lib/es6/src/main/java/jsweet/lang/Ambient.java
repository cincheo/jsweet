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
 * 
 * @deprecated Please use a <code>def.*</code> package for declarations.
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
@Documented
public @interface Ambient {

}
