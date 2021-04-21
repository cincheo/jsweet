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
import static jsweet.util.Lang.string;
import static source.structural.Globals.*;

import def.js.String;
import def.js.RegExp;

public class GlobalsAccess {

	public static void main(String[] args) {
		$export("result", toTitleCase(string("renaud pawlak")));
		test();
	}

	public static void test() {
		foo();
		foo(3);
	}
}

class Globals {

	public static String toTitleCase(String str) {
		return str.toLowerCase().replace(new RegExp("\\w\\S*", "g"), (tok, i) -> {
			return tok.charAt(0).toUpperCase().concat(tok.substr(1).toLowerCase());
		});
	}

	public static void foo() {
		$export("foo0", "true");
	}

	public static void foo(int len) {
		$export("foo1", "true" + len);
	}
}
