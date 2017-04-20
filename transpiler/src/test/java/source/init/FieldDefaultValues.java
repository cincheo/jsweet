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
package source.init;

import static jsweet.util.Lang.$export;

public class FieldDefaultValues {

	String s;
	int i = 1;
	int j;
	static int I;

	public FieldDefaultValues() {
	}

	public static void main(String[] args) {
		FieldDefaultValues c1 = new FieldDefaultValues();

		$export("s1", "value:" + c1.s);
		$export("i1", "value:" + c1.i);
		$export("j1", "value:" + c1.j);

		FieldDefaultValues c2 = new FieldDefaultValues();

		$export("s2", "value:" + c2.s);
		$export("i2", "value:" + c2.i);
		$export("j2", "value:" + c2.j);

		FieldDefaultValues c3 = new FieldDefaultValues();

		$export("s3", "value:" + c3.s);
		$export("i3", "value:" + c3.i);
		$export("j3", "value:" + c3.j);

	}

}

class WithNoConstructor {

	static int I;
	String s;
	int i = 1;
	int j;

}

class SuperClass {

	static int I;
	int i = 1;
	String s;

}

class WithNoSuperClass extends SuperClass {

	static int I;
	int j;

}
