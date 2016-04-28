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
package source.generics;

import static jsweet.util.Globals.$export;

public class InnerClassNotStatic<T> {

	public static void main(String[] args) {
		new InnerClassNotStatic<Integer>().m(1);
	}

	void m(T t) {
		//new InnerClassNotStatic.InnerClass1<String>().m(t, "s");
		new InnerClass1<String>().m(t, "s");
	}

	public class InnerClass1<U> extends C10<T> {

		public void m(T t, U u) {
			$export("value", "test");
		}
		
	}

	public interface I {

	}

}

class C10<T> {
	
}

