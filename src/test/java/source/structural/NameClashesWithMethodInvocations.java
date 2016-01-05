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

import static jsweet.dom.Globals.alert;

public class NameClashesWithMethodInvocations {

	public void m1(boolean alert) {
		// name clash between parameter and method call
		alert("test");
	}

	public void m2() {
		// name clash between local variable and method call
		String alert = "test";
		alert(alert);
	}

	public void m3() {
		// name clash between local variable and method call
		@SuppressWarnings("unused")
		String m2 = "test";
		m2();
	}
	
}
