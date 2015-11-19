/* Copyright 2015 CINCHEO SAS
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
package org.jsweet.test.transpiler.source.typing;

import static jsweet.util.Globals.function;
import jsweet.lang.Function;

public class ClassTypeAsFunction {

	Function field = function(ClassTypeAsFunction.class);
	
	void m2() {
		jsweet.lang.Function func = function(ClassTypeAsFunction.class);
		m1(func);
	}
	
	void m1(Function param) {
		@SuppressWarnings("unused")
		jsweet.lang.Function func = function(ClassTypeAsFunction.class);
	}

	Function m3() {
		return function(ClassTypeAsFunction.class);
	}
	
}
