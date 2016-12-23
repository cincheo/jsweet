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
package source.api;

import def.js.Array;
import def.js.IArguments;
import jsweet.util.Globals;

public class ForeachIteration {

	void m() {
		Array<String> array = new Array<>();
		array.push("a", "b", "c");
		String seq = "";
		for (String s : array) {
			seq+=s;
		}
		Globals.$export("out", seq);
	}

	void m2(IArguments args) {
		for (Object o : args) {
			o.toString();
		}
	}

	public static void main(String[] args) {
		new ForeachIteration().m();
	}
	
}
