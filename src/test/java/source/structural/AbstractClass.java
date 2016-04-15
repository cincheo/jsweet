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

import jsweet.lang.Interface;

public abstract class AbstractClass {

	abstract void m1();

	abstract int m2();

	abstract Object m3();

	abstract String m4();

	abstract boolean m5();

	String regularMethod() {
		return "";
	}

}

interface SuperEntry<V> {

	V getValue();
	
	int m();
	
}

//test invocations on methods defined in an implemented interface
interface Entry<K, V> extends SuperEntry<V> {
	K getKey();

	V getValue();

	V setValue(V value);
	
	default void test() {
		
	}
}

abstract class AbstractMapEntry<K, V> implements Entry<K, V> {

	@Override
	public final boolean equals(Object other) {
		if (!(other instanceof Entry)) {
			return false;
		}
		Entry<?, ?> entry = (Entry<?, ?>) other;
		return Objects.equals(getKey(), entry.getKey()) && Objects.equals(getValue(), entry.getValue());
	}

}

class Objects {
	public static boolean equals(Object o1, Object o2) {
		return false;
	}
}

@Interface
abstract class I {
	String a;
	public abstract void m1(String s);
	public abstract void m2(int i);
}

abstract class AbstractImpl extends I {
	String a;
	@Override
	public void m1(String s) {
	}
}

class Impl extends AbstractImpl {
	@Override
	public void m2(int i) {
	}
	
	@Override
	public void m1(String s) {
		super.m1(s);
	}
}
