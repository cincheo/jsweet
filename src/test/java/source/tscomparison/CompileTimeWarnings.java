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
package source.tscomparison;

import static jsweet.dom.Globals.alert;
import static jsweet.dom.Globals.console;

enum DemoEnum {
	VALUE,
	VALUE2,
	FORGOTTEN_VALUE
}

public class CompileTimeWarnings {
	public static void main(String[] args) {
		
		int a = 1;
		
		// compile time warning
		if (a == a) {
			alert("please help me, I won't print!");
		}
		
		// compile time warning
		a = a;
		
		DemoEnum nullInstance = null;
		alert(nullInstance.name()); // null warning
		
		DemoEnum e = DemoEnum.FORGOTTEN_VALUE;
		switch (e) { // missing 'case' warning
		case VALUE:
			alert("I am good, I am handled.");
			break;
		case VALUE2:
			alert("I am good, I am handled.");
			break;
		}
	}
}
