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
package source.typing;

import static jsweet.util.Lang.$export;

import jsweet.util.tuple.Tuple2;

public class Tuples {

	static Tuple2<String, Integer> tuple = new Tuple2<String, Integer>("test", 10);
	
	public static void main(String[] args) {
		$export("0_init", tuple.$0);
		$export("1_init", tuple.$1);
		tuple.$0 = "ok";
		tuple.$1--;
		$export("0_end", tuple.$0);
		$export("1_end", tuple.$1);
	}
	
}
