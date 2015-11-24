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
package source.overload;

import static jsweet.util.Globals.$export;

public class Overload {

	String m() {
		return this.m("default");
	}

	String m(String s) {
		return this.m(s, 1);
	}

	String m(String s, int i) {
		return s + i;
	}

	public static void main(String[] args) {
		Overload o = new Overload();
		$export("res1", o.m());
		$export("res2", o.m("s1"));
		$export("res3", o.m("s2", 2));
	}

}
