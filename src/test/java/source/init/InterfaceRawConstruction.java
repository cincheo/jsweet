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

import jsweet.lang.Interface;

public class InterfaceRawConstruction extends jsweet.lang.Object {

	DataStruct6 d2 = new DataStruct6() {
	};

	DataStruct6 d3 = new DataStruct6() {
		{
		}
	};

	I i1 = new I();

	I i2 = new I() {
	};

	I i3 = new I() {
		{
			number = 0;
		}
	};

}

@Interface
abstract class DataStruct6 {
	public native String $get(String userId);
}

@Interface
class I {
	int number;
}
