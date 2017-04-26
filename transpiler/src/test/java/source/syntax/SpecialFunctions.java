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
package source.syntax;

import static jsweet.util.Lang.$export;
import static jsweet.util.Lang.$apply;
import static jsweet.util.Lang.$new;

import java.util.function.Function;

public class SpecialFunctions {

	void m(ApplyI1 a1, ConstrI1 c1) {
		a1.apply();
		a1.apply("test");
		c1.$new("test");
		
		Function<Object, Object> f = x -> x;
		f.apply(null);
	}
	void m3(ApplyI2 a1) {
		a1.$apply();
		a1.$apply("test");
	}

	void m2(Object o) {
		@SuppressWarnings("unused")
		Object result1 = $apply(o, "param");
		@SuppressWarnings("unused")
		Object result2 = $new(o, "param");
	}
	
}

@jsweet.lang.Interface
abstract class ApplyI1 extends def.js.Object {
	abstract public String apply();

	abstract public ApplyI1 apply(String a);
}

@jsweet.lang.Interface
abstract class ApplyI2 extends def.js.Object {
	abstract public String $apply();

	abstract public String $apply(String a);
}

@jsweet.lang.Interface
abstract class ConstrI1 extends def.js.Object {
	abstract public ConstrI1 $new(String a);
}
