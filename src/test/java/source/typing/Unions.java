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
package source.typing;

import static jsweet.util.Globals.$export;
import static jsweet.util.Globals.union;
import static jsweet.util.Globals.function;

import jsweet.lang.Function;
import jsweet.util.union.Union;
import jsweet.util.union.Union4;

public class Unions {

	static Union<String, Integer> u;
	static Union4<String, Integer, Boolean, Function> u4;

	static void m(Union<String, Integer> union) {
		$export("union", union);
		String s = union(union);
		$export("s", s);
	}

	public static void main(String[] args) {
		m(union("test"));
		u = union("test");
		u4 = union("test");
		u4 = union(1);
		u4 = union(true);
		u4 = union(function(() -> {
		}));
	}

}
