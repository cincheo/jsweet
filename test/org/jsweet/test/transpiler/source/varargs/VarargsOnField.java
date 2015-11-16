/* Copyright 2015 CINCHEO SAS
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
package org.jsweet.test.transpiler.source.varargs;

import static jsweet.util.Globals.$export;

public class VarargsOnField {
	public static void main(String[] args) {
		new VarargsOnField().call("field", "access");
	}
	
	private A field = new A();
	
	public void call(String... args) {
		field.b(args);
	}
}

class A {
	public void b(String... args) {
		this.c(args);
	}

	public void c(String... args) {
		$export("argsLength", args.length);
		$export("firstArg", args[0]);
	}
}
