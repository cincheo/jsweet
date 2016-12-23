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

public class QualifiedInstantiation {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		def.js.Array<String> array = new def.js.Array<String>();
		def.js.Array<def.js.String> array2 = new def.js.Array<def.js.String>();
		def.js.String string = new def.js.String("1");
		def.js.Number number = new def.js.Number("3.0");
		def.js.Date date = new def.js.Date("2015-05-01");
		def.js.Error error = new def.js.Error("bloody error");
		def.js.RegExp regex = new def.js.RegExp("\\d", "g");
	}
}
