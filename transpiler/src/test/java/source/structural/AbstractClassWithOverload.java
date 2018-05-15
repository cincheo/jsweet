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
package source.structural;

import static jsweet.util.Lang.$export;

import jsweet.lang.Interface;

@Interface
abstract class InterfaceWithOverload {
	public abstract void overload(int i);
}

public abstract class AbstractClassWithOverload extends InterfaceWithOverload {
	public void overload(int i, String che) {
		$export("overload_int_string_called", i + ";" + che);
		overload(i);
		System.out.println("was there => " + che);
	}
}

class ClassWithInheritedOverload extends AbstractClassWithOverload {
	public static void main(String[] args) {
		System.out.println("start ");

		new ClassWithInheritedOverload().overload(68, "PARAMSTR");
	}

	@Override
	public void overload(int i) {
		$export("overload_int_called", i);
		System.out.println("overload(" + i + ")");
	}
}