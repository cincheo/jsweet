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
package source.structural.globalclasses.a;

import static jsweet.util.Lang.object;

class Globals {

	public static int a;

	public Globals() {
		a = 3;
	}
}

public class GlobalsConstructor {

	public void main(String[] args) {
		@SuppressWarnings("unused")
		Globals g = new Globals();
		
		new Globals() {
			{
				object(this).$set("b", 6);
			}
		};
	}

}

class Invalid extends Globals {
	
}