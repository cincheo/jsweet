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
package source.api;

import jsweet.lang.Function;
import jsweet.lang.String;
import jsweet.lang.Number;
import jsweet.lang.Boolean;
import jsweet.lang.JSON;

public class PrimitiveInstantiation {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		String s1 = new String(1.0);
		String s2 = new String("1");
		String s3 = new String();
		
		Number n1 = new Number(2.0);
		Number n2 = new Number("3.0");
		Number n3 = new Number();
		
		Boolean b1 = new Boolean();
		Boolean b2 = new Boolean("3.0");
		
		Function f = new Function("alert('success')");
		
		Object o = JSON.parse("test");
	}
}
