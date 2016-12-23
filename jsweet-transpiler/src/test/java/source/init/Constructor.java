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

import static jsweet.util.Globals.$export;

public class Constructor {

	String s;

	public Constructor() {
		this("default");
	}

	public Constructor(String s) {
		this.s = s;
	}

	public static void main(String[] args) {
		Constructor c = new Constructor("abc");

		$export("v1", c.s);

		c = new Constructor();

		$export("v2", c.s);

		c = new Constructor() {
			{
				s = "test";
			}
		};

		$export("v3", c.s);
	}

}
