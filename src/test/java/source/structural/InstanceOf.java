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

import static jsweet.util.Globals.typeof;

import jsweet.lang.Array;

import static jsweet.util.Globals.equalsStrict;
import static jsweet.lang.Globals.eval;

public class InstanceOf {
	public static void main(String[] args) {
		Number n1 = 2;
		Object n2 = 2;
		int n3 = 2;
		Object s = "test";
		C2 c = new C2();
		Object anArray = new int[0];
		InstanceOf object = new InstanceOf();
		
		assert n1 instanceof Number;
		assert n2 instanceof Number;
		assert n2 instanceof Integer;
		assert !(n2 instanceof String);
		assert !(n2 instanceof Array);
		assert s instanceof String;
		assert !(s instanceof Integer);
		assert c instanceof C2;
		assert typeof(n3) == "number";
		assert equalsStrict(typeof(n3), "number");

		assert ((String) eval("typeof n3")) == "number";

		assert anArray instanceof Array;

		assert object instanceof InstanceOf;

	}

}

class C2 {

}