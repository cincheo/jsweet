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

import static jsweet.util.Globals.$get;
import static jsweet.util.Globals.$set;
import static jsweet.util.Globals.$delete;
import static jsweet.util.Globals.$export;

public class IndexedAccessInStaticScope {

	static {
//		$get("a");
//		$set("a", "value");
//		jsweet.util.Globals.$get("a");
//		jsweet.util.Globals.$set("a", "value");
	}
	
	public static void m() {
//		$get("a");
//		$set("a", "value");
//		jsweet.util.Globals.$get("a");
//		jsweet.util.Globals.$set("a", "value");
//		
//		$set("c", "i want to be deleted");
//		$delete("c");
	}
	
	public static void main(String[] args) {
//		m();
//		$export("out_a", $get("a"));
//		$export("out_b", $get("b"));
//		$export("out_c", $get("c"));
	}
	
}

class C {
	public static void m() {
//		$set("b", "value");
	}
}