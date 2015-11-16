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

import static org.jsweet.test.transpiler.source.syntax.Globals.toTitleCase;
import jsweet.lang.RegExp;
import static jsweet.util.Globals.string;

public class GlobalsInvocation {

	String firstName;
	String lastName;

	public String getFullName() {
		return toTitleCase(firstName + " " + lastName);
	}
	
	public void m() {
		Globals.m();
		org.jsweet.test.transpiler.source.syntax.Globals.m();
	}
}

class Globals {
	public static String toTitleCase(String str) {
		return string(str).replace(new RegExp("/\\w\\S*/g"), (tok, i) -> {
			jsweet.lang.String txt = string(tok);
			return string(txt.charAt(0)).toUpperCase() + string(txt.substr(1)).toLowerCase();
		});
	}
	public static void m() {};
}
