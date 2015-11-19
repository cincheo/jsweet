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

import static jsweet.util.Globals.union;
import jsweet.util.union.Union;

public class WrongUnions {

	@SuppressWarnings("unused")
	static void m(Union<String, Integer> union) {
		String s = union(union);
		boolean b = union(union); // wrong
		s = union(union);
		b = union(union); // wrong
		m1(union(union), union(union));
		m2(union(union), union(union)); // wrong
	}

	static void m1(String s, int i) {
	}

	static void m2(String s, boolean b) {
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		m(union("test"));
		m(union(true)); // wrong
		Union<String, Integer> u1 = union("test");
		Union<String, Integer> u2 = union(false); // wrong
		u1 = union("test");
		u2 = union(false); // wrong
	}

}
