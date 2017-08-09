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
import static jsweet.util.Lang.object;

import def.js.Date;
import jsweet.lang.Interface;

public class ValidIndexedAccesses {

	{
		// deprecated
		// $get(this, "a");
		// $set(this, "a", "value");
		// jsweet.util.Globals.$get(this, "a");
		// jsweet.util.Globals.$set(this, "a", "value");
		object(this).$get("a");
		object(this).$set("a", "value");
		jsweet.util.Lang.object(this).$get("a");
		jsweet.util.Lang.object(this).$set("a", "value");
	}

	public ValidIndexedAccesses() {
		// deprecated
		// $get(this, "a");
		// $set(this, "a", "value");
		// jsweet.util.Globals.$get(this, "a");
		// jsweet.util.Globals.$set(this, "a", "value");
		object(this).$get("a");
		object(this).$set("a", "value");
		jsweet.util.Lang.object(this).$get("a");
		jsweet.util.Lang.object(this).$set("a", "value");
	}

	public void m() {
		// deprecated
		// $get(this, "a");
		// $set(this, "a", "value");
		// jsweet.util.Globals.$get(this, "a");
		// jsweet.util.Globals.$set(this, "a", "value");
		object(this).$get("a");
		object(this).$set("a", "value");
		jsweet.util.Lang.object(this).$get("a");
		jsweet.util.Lang.object(this).$set("a", "value");
		ValidIndexedAccesses2 o1 = new ValidIndexedAccesses2();
		o1.$get("a");
		o1.$set("a", "value");
		new ValidIndexedAccesses2() {
			{
				$set("a", "value");
				// deprecated
				// jsweet.util.Globals.$set(this, "a2", "value");
			}
		};
		new ValidIndexedAccesses() {
			{
				// deprecated
				// $set(this, "a", "value");
				// jsweet.util.Globals.$set(this, "a2", "value");
				object(this).$set("a", "value");
				jsweet.util.Lang.object(this).$set("a", "value");
			}
		};

	}

	public static void m1() {
		new ValidIndexedAccesses() {
			{
				// deprecated
				// $set(this, "a", "value");
				// jsweet.util.Globals.$set(this, "a", "value");
				object(this).$set("a", "value");
				jsweet.util.Lang.object(this).$set("a", "value");
			}
		};
	}

	public static void main(String[] args) {

		def.js.Object validAccesses = new def.js.Object() {
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

		Object validAccesses2 = new Object();

		// deprecated
		// $set(validAccesses2, "field4", "value4");
		object(validAccesses2).$set("field4", "value4");
		object(validAccesses2).$set("field5", "value5");

		// deprecated
		// $export("field4", $get(validAccesses2, "field4"));
		$export("field4", object(validAccesses2).$get("field4"));
		$export("field5", object(validAccesses2).$get("field5"));

	}
}

class ValidIndexedAccesses2 extends def.js.Object {

	{
		$get("a");
		$set("a", "value");
		// deprecated
		// jsweet.util.Globals.$get(this, "a");
		// jsweet.util.Globals.$set(this, "a", "value");
	}

	public ValidIndexedAccesses2() {
		$get("a");
		$set("a", "value");
		// deprecated
		// jsweet.util.Globals.$get(this, "a");
		// jsweet.util.Globals.$set(this, "a", "value");
	}

	public void m() {
		$get("a");
		$set("a", "value");
		// deprecated
		// jsweet.util.Globals.$get(this, "a");
		// jsweet.util.Globals.$set(this, "a", "value");
		Test t = new Test() {
			{
			}
		};
		@SuppressWarnings("unused")
		String s = t.$get("test");
		AClass2 c2 = new AClass2();
		@SuppressWarnings("unused")
		Date d = c2.$get("test");
	}

}

@Interface
class Test {
	public native String $get(String name);
}

interface Test2 {
	public Date $get(String name);
}

class AClass2 implements Test2 {
	@Override
	public native Date $get(String name);
}
