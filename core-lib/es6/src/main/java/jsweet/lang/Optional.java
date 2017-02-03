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
@Documented
public @interface Optional {

}
