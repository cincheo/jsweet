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

import static jsweet.util.Lang.$export;

import def.js.Array;

class AClass<T> {
	String s;

	public AClass(String s) {
		this.s = s;
	}
}

interface AnInterface {
	int compare(String s);
}

interface AnInterface2 {
	int accept(AnonymousClass<String> c);
}

abstract class AnAbstractClass<T> {
	public abstract void m();
}

public class AnonymousClass<E> {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {
		new AnonymousClass<String>("test").m();
	}

	static AnInterface2 anInterface;

	static {
		anInterface = new AnInterface2() {

			@Override
			public int accept(AnonymousClass<String> c) {
				return c.i;
			}
		};
	}

	private int i = 2;

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

	void m3(AnAbstractClass<E> c) {
	}

	void m2(final E[] finalString) {
		m3(new AnAbstractClass<E>() {

			@Override
			public void m() {
				trace.push("" + i + getI() + finalString);
			}
		});

	}

	static AnInterface get() {
		return new AnInterface() {
			@Override
			public int compare(String s) {
				return s.length();
			}
		};
	}

	// public AnonymousClass() {
	// }

	public AnonymousClass(AnAbstractClass<E> o) {
		o.m();
	}

	public AnonymousClass(final String finalString) {
		this(new AnAbstractClass<E>() {

			@Override
			public void m() {
				trace.push(finalString);
			}
		});
	}

}
