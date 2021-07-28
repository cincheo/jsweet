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

public class StaticAnonymousClass<T> {

	static <T> Interface1<T> m(T t) {
		return new Interface1<T>() {
			@Override
			public Interface2<T> m1(T t) {
				return null;
			}
		};
	}

	static void m() {
		new Interface1<String>() {
			@Override
			public Interface2<String> m1(String t) {
				return null;
			}
		};
	}

	static <T> Interface1<T> nested() {
		return new Interface1<T>() {
			@Override
			public Interface2<T> m1(T param) {
				return new Interface2<T>() {
					public T nextEntry = null;

					@Override
					public void m2(T next) {
						nextEntry = next;
						System.out.println(nextEntry);
					}
				};
			}
		};
	}

}

interface Interface1<T> {
	Interface2<T> m1(T t);
}

interface Interface2<T> {
	void m2(T t);
}
