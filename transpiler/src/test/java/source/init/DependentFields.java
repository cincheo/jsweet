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

class Foo {
	
	Foo(int val, int val2) {
		$export("addResult", val + 5);
		$export("addResult2", val2 + 5);
	}
}

public class DependentFields {

	int i = 1;
	int j;
	Foo foo = new Foo(i,j);

	public DependentFields() {
	}

	public static void main(String[] args) {
		DependentFields c1 = new DependentFields();

		$export("i1", "value:" + c1.i);
		$export("j1", "value:" + c1.j);
	}
}