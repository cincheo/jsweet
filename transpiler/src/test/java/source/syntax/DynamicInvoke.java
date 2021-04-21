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
package source.syntax;

import static jsweet.util.Lang.$export;
import static jsweet.util.Lang.object;

public class DynamicInvoke {

	{
		object(this).$invoke("a");
		jsweet.util.Lang.object(this).$invoke("b");
	}

	public DynamicInvoke() {
		object(this).$invoke("c");
		jsweet.util.Lang.object(this).$invoke("d");
	}

	public void a_1() {
		$export("a_1", "true");
	}

	public void a() {
		object(this).$invoke("a_1");
		$export("a", "true");
	}

	public void b() {
		$export("b", "true");
	}

	public void c() {
		$export("c", "true");
	}

	public void d() {
		$export("d", "true");
	}

	public void e() {
		$export("e", "true");
	}

	public void f(Integer i, Boolean b, String s) {
		$export("f", i + ";" + b + ";" + s);
	}

	public static void main(String[] args) {

		DynamicInvoke test = new DynamicInvoke();
		object(test).$invoke("e");
		object(test).$invoke("f", 5, new Boolean(true), "foo");

	}
}
