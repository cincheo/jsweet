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

import static jsweet.util.Globals.$export;

import java.util.function.BiConsumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public class References {

	void m(String s, int i) {
		$export("s", s);
		$export("i", i);
		$export("m", m(MyObject::new));
		$export("m2", m2(MyObject[]::new));
	}

	void m1(BiConsumer<String, Integer> c) {
		c.accept("foo", 5);
	}

	public static void main(String[] args) {
		References r = new References();
		r.m1(r::m);
	}

	public static <T> Object m(Supplier<T> supplier) {
		return supplier.get();
	}

	public static <T> T[] m2(IntFunction<T[]> intFunction) {
		return intFunction.apply(2);
	}
	
}

class MyObject {
	@Override
	public String toString() {
		return "O";
	}
}