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

import jsweet.lang.Array;

class AClass<T> {
	String s;

	public AClass(String s) {
		this.s = s;
	}
}

interface AnInterface {
	int compare(String s);
}

public class AnonymousClass {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {
		new AnonymousClass().m();
	}

	int i = 2;

	int getI() {
		return i;
	}

	class AClass2<T> {
		String s;

		public AClass2(String s) {
			this.s = s;
		}
	}

	void m() {
		new AClass<Integer>("a") {
			@SuppressWarnings("unused")
			void m() {
			}
		};
		final String finalString = "final";
		new AClass<String>("abc") {

			public void m1() {
				trace.push("" + i + getI() + s + finalString);
				new AClass<String>("a") {
					@SuppressWarnings("unused")
					void m() {
						new AClass2<Integer>("a") {
							void m() {
							}
						};
					}
				};
			}

		}.m1();
		$export("trace", trace.join(","));
		new AClass2<Integer>("a") {
			@SuppressWarnings("unused")
			void m() {
			}
		};
	}

	static AnInterface get() {
		return new AnInterface() {
			@Override
			public int compare(String s) {
				return s.length();
			}
		};
	}
	
}
