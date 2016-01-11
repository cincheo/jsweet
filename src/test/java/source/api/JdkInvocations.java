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
package source.api;

import static jsweet.util.Globals.$export;

public class JdkInvocations {

	String s = "test";

	void m() {
		try {
			System.out.println("test");
			System.err.println("test");
		} catch (Exception e) {
		}
		toString();
		this.toString();
		$export("s1", s.toString());
		$export("s2", m1().toString());
		$export("s3", s.charAt(1));
		$export("s4", s.concat("c"));
		$export("i1", s.indexOf("s"));
		$export("i2", s.lastIndexOf("gg"));
		$export("l", s.length());
		s.lastIndexOf("", 10);
		Other4 o2 = new Other4();
		o2.toString();
	}

	String m1() {
		return "m1";
	}

	public static void main(String[] args) {
		new JdkInvocations().m();
	}

}

class Other4 extends Object {
	void m() {
		toString();
		this.toString();
	}
}