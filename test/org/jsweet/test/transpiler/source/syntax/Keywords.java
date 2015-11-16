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
package org.jsweet.test.transpiler.source.syntax;

public class Keywords {

	void var(String s, int i) {
	}

	void function(String typeof, int i) {
		typeof = "";
	}

	@SuppressWarnings("unused")
	void m() {
		Integer var = null;
		String function = null;
	}

	void m2(int var, long function) {
		var = 2;
		var = (int) function;
		@SuppressWarnings("unused")
		String constructor = "abc";
	}

}

class Other1 {

	int var;

	String function;

}

class Other2 {

	public void function() {
	}

	public int var() {
		this.function();
		return 0;
	}

}