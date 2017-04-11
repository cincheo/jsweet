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

import static jsweet.util.Globals.$export;

public class InnerClass {

	public static void main(String[] args) {
		new InnerClass.InnerClass1().m1();
		new InnerClass().m();
		InnerClass1.m2();
		String s = InnerClass1.S;
		$export("value5", s);
	}

	public void m() {
		new InnerClass.InnerClass2().m2();
		new InnerClass2().m2();
	}

	static int i = 1;

	static int parentMethod() {
		return 4;
	}

	private final static class InnerClass1 {

		private static String S = "abc";
		static final int I = 2;

		private void m1() {
			$export("value1", "test" + i);
			$export("value4", "test" + parentMethod());
		}

		public static void m2() {
			$export("value3", "test3");
		}
	}

	public static class InnerClass2 {

		public void m2() {
			$export("value2", "test2");
		}

		public static void main(String[] args) {

		}
	}

	public interface I {

	}

}
