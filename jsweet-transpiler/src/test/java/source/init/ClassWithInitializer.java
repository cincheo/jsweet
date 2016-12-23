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
import jsweet.lang.ObjectType;

class DataStruct12 {
	String field;

	public void setField(String f) {
		field = f;
	}
}

public class ClassWithInitializer extends def.js.Object {

	DataStruct10 d3 = new DataStruct10() {
		{
			field = "a";
		}
	};

	DataStruct11 d4 = new DataStruct11() {
		{
			field = "a";
		}
	};

	DataStruct12 d5 = new DataStruct12() {
		{
			setField("a");
		}
	};

	DataStruct12 d6 = new DataStruct12() {
		{
			field = "a";
		}
	};

}

@Interface
abstract class DataStruct10 {
	String field;
}

@ObjectType
abstract class DataStruct11 {
	String field;
}
