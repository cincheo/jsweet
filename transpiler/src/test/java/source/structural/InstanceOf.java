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

import static def.js.Globals.eval;
import static jsweet.util.Lang.any;
import static jsweet.util.Lang.equalsStrict;
import static jsweet.util.Lang.typeof;

import def.js.Array;
import jsweet.lang.Interface;

public class InstanceOf {
	public static void main(String[] args) {
		Number n1 = 2;
		Object n2 = 2;
		int n3 = 2;
		Object s = "test";
		C2 c = new C2();
		Object anArray = new int[0];
		InstanceOf object = new InstanceOf();

		Interface1 i1 = new Interface1() {
			{
			}
		};
		Interface2 i2 = new Interface2() {
			{
			}
		};
		Interface3 i3 = new Interface3() {
			@Override
			public String m1() {
				return "m1";
			}
		};
		Interface4 i4 = new Interface4() {
			@Override
			public String m1() {
				return "m12";
			}

			@Override
			public String m2() {
				return "m2";
			}
		};

		assert n1 instanceof Number;
		assert n2 instanceof Number;
		assert n2 instanceof Integer;
		assert !(n2 instanceof String);
		assert !(any(n2) instanceof Array);
		assert s instanceof String;
		assert !(s instanceof Integer);
		assert c instanceof C2;
		assert typeof(n3) == "number";
		assert equalsStrict(typeof(n3), "number");

		assert ((String) eval("typeof n3")) == "number";

		assert anArray instanceof Array;

		assert object instanceof InstanceOf;

		assert c instanceof C1;

		assert i1 instanceof Interface1;
		assert i2 instanceof Interface2;
		assert i3 instanceof Interface3;
		assert i3.m1().equals("m1");
		assert i4 instanceof Interface4;
		assert i4.m1().equals("m12");
		assert i4.m2().equals("m2");
	}

}

class C1 {

}

class C2 extends C1 {

}

@Interface
abstract class Interface1 {
}

@Interface
abstract class Interface2 extends Interface1 {
}

interface Interface3 {
	String m1();
}

interface Interface4 extends Interface3 {
	String m2();
}
