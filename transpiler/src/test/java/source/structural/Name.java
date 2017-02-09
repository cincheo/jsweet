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
package source.structural;

import def.js.Array;

public class Name {

	@jsweet.lang.Name("name-with-dash")
	String name_with_dash;

	@jsweet.lang.Name("static-name-with-dash")
	static String static_name_with_dash;

	static Array<String> array;


	@jsweet.lang.Name("method-name-with-dash")
	void method_name_with_dash() {}

	@jsweet.lang.Name("static-method-name-with-dash")
	static void static_method_name_with_dash() {}
	
	public static void main(String[] args) {
		array.reduceCallbackfnFunction4((s1, s2, d, s3) -> {
			return null;
		}, null);
		String s = static_name_with_dash;
		s = Name.static_name_with_dash;
		
		static_method_name_with_dash();
		Name.static_method_name_with_dash();
		
		Name n = new Name();
		n.method_name_with_dash();
		n.test();
		n.method_name_with_dash();
	}

	private void test() {
		method_name_with_dash();
		this.method_name_with_dash();
	}
	
	

}
