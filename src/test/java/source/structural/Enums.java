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

import static jsweet.util.Globals.array;
import static jsweet.util.Globals.$export;

public class Enums {

	public static void main(String[] args) {
		MyEnum e = MyEnum.A;
		$export("value", e);
		$export("nameOfA", e.name());
		$export("ordinalOfA", e.ordinal());
		$export("valueOfA", MyEnum.valueOf("A"));
		$export("valueOfC", array(MyEnum.values()).indexOf(MyEnum.valueOf("C")));
	}

}

enum MyEnum {
	A, B, C
}

class C {
	public void m() {
	}
}