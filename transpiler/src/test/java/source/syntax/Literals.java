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

public class Literals {

	public static void main(String[] args) {
		long l = 3253l;
		l = 1L;
		$export("l", l);
		System.out.println(l);

		float f = 123f;
		f = 1F;
		$export("f", f);
		System.out.println(f);

		//String s = "c'est l'été!\n\\cool";
		String s = "c'est l'été!";
		$export("s", s);
		System.out.println(s);

		char c = 'é';
		$export("c", c);
		System.out.println(c);
	}
}
