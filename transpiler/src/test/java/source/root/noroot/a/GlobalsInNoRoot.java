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
package source.root.noroot.a;

import static jsweet.util.Lang.$export;
import static source.root.noroot.a.Globals.m2;

public class GlobalsInNoRoot {

	public void f1() {
		Globals.m1();
	}

	public void f2() {
		m2();
	}
}

class Globals {

	public static void m1() {
		$export("m1", true);
	}

	public static void m2() {
		$export("m2", true);
	}

	public static void main(String[] args) {
		GlobalsInNoRoot o = new GlobalsInNoRoot();
		o.f1();
		o.f2();
	}

}
