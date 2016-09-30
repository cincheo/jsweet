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

@SuppressWarnings("unused")
public class InstantiationWithGenerics<T, V> {

	T t;
	private V value;

	public InstantiationWithGenerics(T input, V value) {
		this.t = input;
		this.value = value;
	}

	public static void main(String[] args) {
		InstantiationWithGenerics<String, Integer> foo = new InstantiationWithGenerics<String, Integer>("lolo", 4);
		source.generics.InstantiationWithGenerics<String, Integer> bar = new source.generics.InstantiationWithGenerics<String, Integer>("lolo", 4);
		C<String> c = new C<String>();
	}
}

class C<T> {
}
