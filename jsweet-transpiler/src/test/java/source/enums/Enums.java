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
package source.enums;

import static jsweet.util.Globals.$export;
import static jsweet.util.Globals.array;

import jsweet.lang.Error;
import source.enums.other.EnumInOtherPackage;

public class Enums {

	static MyEnum e = MyEnum.B;

	public static void main(String[] args) {
		MyEnum e = MyEnum.A;
		EnumInSamePackage e2 = EnumInSamePackage.V1;
		EnumInOtherPackage e3 = EnumInOtherPackage.V1;
		$export("value", e);
		$export("nameOfA", e.name());
		$export("ordinalOfA", e.ordinal());
		$export("valueOfA", MyEnum.valueOf("A").name());
		$export("valueOfC", array(MyEnum.values()).indexOf(MyEnum.valueOf("C")));
		$export("ref", Enums.e.name());
		String s = null;
		switch (e) {
		case A:
			s = "A";
			break;
		case B:
			s = "B";
			break;
		case C:
			s = "C";
			break;
		}
		$export("switch", s);
		switch (e2) {
		case V1:
			break;
		case V2:
			throw new Error();
		default:
			throw new Error();
		}
		switch (e3) {
		case V1:
			break;
		case V2:
			throw new Error();
		default:
			throw new Error();
		}
	}

}

enum MyEnum {
	A, B, C
}

class C {
	public void m() {
	}
}