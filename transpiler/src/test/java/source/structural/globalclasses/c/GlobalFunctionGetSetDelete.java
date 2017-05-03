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
package source.structural.globalclasses.c;

import static jsweet.util.Lang.object;

@SuppressWarnings("all")
class Globals {

	public static void test() {
		Object val;
		val = object(new GlobalFunctionGetSetDelete()).$get("ttest");
		object(new GlobalFunctionGetSetDelete()).$set("ttest", val);
		object(new GlobalFunctionGetSetDelete()).$delete("ttest");
		val = object(GlobalFunctionGetSetDelete.class).$get("ttest");
		object(GlobalFunctionGetSetDelete.class).$set("ttest", val);
		object(GlobalFunctionGetSetDelete.class).$delete("ttest");

		// valid
		def.js.Object otherObject = null;
		otherObject.$get("test");
		otherObject.$set("test", val);
		otherObject.$delete("test");
	}
}

public class GlobalFunctionGetSetDelete {

	public void main(String[] args) {
	}
}
