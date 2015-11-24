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

import static jsweet.util.Globals.$delete;
import static jsweet.util.Globals.$export;
import static jsweet.util.Globals.$get;
import static jsweet.util.Globals.$set;

public class ValidIndexedAccesses {

	{
		$get("a");
		$set("a", "value");
		jsweet.util.Globals.$get("a");
		jsweet.util.Globals.$set("a", "value");
	}

	public ValidIndexedAccesses() {
		$get("a");
		$set("a", "value");
		jsweet.util.Globals.$get("a");
		jsweet.util.Globals.$set("a", "value");
	}

	public void m() {
		$get("a");
		$set("a", "value");
		jsweet.util.Globals.$get("a");
		jsweet.util.Globals.$set("a", "value");
		ValidIndexedAccesses2 o1 = new ValidIndexedAccesses2();
		o1.$get("a");
		o1.$set("a", "value");
		new ValidIndexedAccesses2() {
			{
				$set("a", "value");
				jsweet.util.Globals.$set("a", "value");
			}
		};
		new ValidIndexedAccesses() {
			{
				$set("a", "value");
				jsweet.util.Globals.$set("a", "value");
			}
		};

	}
	
	public static void m1() {
		new ValidIndexedAccesses() {
			{
				$set("a", "value");
				jsweet.util.Globals.$set("a", "value");
			}
		};
	}

	public static void main(String[] args) {
		
		jsweet.lang.Object validAccesses = new jsweet.lang.Object() {
			{
				$set("field1", "value");
				$set("field2", "to be deleted");
			}
		};
		
		validAccesses.$delete("field2");
		
		validAccesses.$set("field3", "to be deleted");
		validAccesses.$delete("field3");
		
		$export("field1", validAccesses.$get("field1"));
		$export("field2", validAccesses.$get("field2"));
		$export("field3", validAccesses.$get("field3"));
	}
}

class ValidIndexedAccesses2 extends jsweet.lang.Object {

	{
		$get("a");
		$set("a", "value");
		jsweet.util.Globals.$get("a");
		jsweet.util.Globals.$set("a", "value");
	}

	public ValidIndexedAccesses2() {
		$get("a");
		$set("a", "value");
		jsweet.util.Globals.$get("a");
		jsweet.util.Globals.$set("a", "value");
	}

	public void m() {
		$get("a");
		$set("a", "value");
		jsweet.util.Globals.$get("a");
		jsweet.util.Globals.$set("a", "value");
	}

}
