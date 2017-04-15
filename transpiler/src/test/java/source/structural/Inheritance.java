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
import static jsweet.util.Globals.$get;

import def.js.Array;
import jsweet.lang.Interface;
import def.js.JSON;

import java.util.Iterator;

class AloneInTheDarkClass extends def.js.Object {
	public AloneInTheDarkClass() {
		// super() call shouldn't be generated
		int i = 5;
	}
}

@Interface
abstract class SuperInterface1 {
}

class SuperClass1 extends SuperInterface1 {
	public SuperClass1() {
	}

	protected void m() {
	};

	protected Array<String> a;
}

public class Inheritance extends SuperClass1 {
	public Inheritance() {
		super();
		m();
		super.m();
		this.m();
		Array<String> v = a;
		v = super.a;
		v = this.a;
		super.a.push("a");
		this.a.push("b");
		a.pop();
	}

	public static void main(String[] args) {
		B b = new B();
		$export("X", b instanceof X);
		$export("Y", b instanceof Y);
		$export("s1b", $get(b, "s1"));
		$export("s2b", $get(b, "s2"));
		String s = JSON.stringify(b);
		Object o = JSON.parse(s);
		// by default, serialization looses types
		$export("itfo", o instanceof X);
		$export("s1o", $get(o, "s1"));
		$export("s2o", $get(o, "s2"));
	}

}

@Interface
abstract class SubInterface extends SuperInterface1 {
}

// TODO: this is weird... it works in Typescript... check what it means
@Interface
abstract class SubInterface1 extends SuperClass1 {
}

interface X {
}

interface Y {
}

class A implements X {
	String s2 = "s2";
}

class B extends A implements Y {
	String s1 = "s1";
}

class D extends SubInterface {
}

interface Shape {

	public Rectangle getBounds();

	public Rectangle2D getBounds2D();

}

class Rectangle2D implements Shape {

	public Rectangle getBounds() {
		return null;
	}

	public Rectangle2D getBounds2D() {
		return null;
	}

}

class Rectangle extends Rectangle2D implements Shape {

}

class SomeShape implements Shape {

	public Rectangle getBounds() {
		return getBounds2D().getBounds();
	}

	public Rectangle2D getBounds2D() {
		return null;
	}

}

abstract class AbstractIterator<T> implements Iterator<T> {
	@Override
	public final T next() { return null; }
}
