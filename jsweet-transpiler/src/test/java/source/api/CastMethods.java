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

import static jsweet.util.Globals.array;
import static jsweet.util.Globals.bool;
import static jsweet.util.Globals.object;

import def.js.Boolean;
import def.js.Number;
import jsweet.util.Globals;

public class CastMethods {

	@SuppressWarnings("unused")
	void m() {
		Boolean b1 = Globals.bool(true);
		b1 = bool(true);
		boolean b2 = Globals.bool(b1);
		b2 = bool(b1);
		if (b2) {
			b1 = bool(b2);
			int[] array = {};
			Globals.array(array).push(1, 2, 3);
			array(array).push(1, 2, 3);
			int i = array(array(array))[1];
			m1(b1);
			m1(bool(b2));
			m2(bool(b1));
			m2(b2);
		}
		Number n = Globals.number(1);
		n.toLocaleString();
		n.toFixed();
		int i = Globals.integer(n);
		double d = Globals.number(n);
		object("");
	}
	
	void m1(Boolean b) {}

	void m2(boolean b) {}
	
}
