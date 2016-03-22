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
package source.overload;

import static jsweet.util.Globals.$export;
import static jsweet.util.Globals.array;

public class WrongOverload {

	static String[] trace = {};

	public static void main(String[] args) {
		WrongOverload o = new WrongOverload();
		o.draw("s");
		o.draw("s", 2);
		o.draw(new MyClass(), 17);
		drawStatic(1, true, 2);
		int i = drawStatic("1234567");
		array(trace).push("" + i);
		$export("trace", array(trace).join());
	}

	public WrongOverload() {
		draw();
	}

	public void draw() {
		array(trace).push("1");
	}

	public String draw(String s) {
		System.out.println(s);
		array(trace).push("2");
		return s;
	}

	public void draw(String s, int i) {
		System.out.println(s);
		System.out.println(i);
		array(trace).push("3");
	}

	public void draw(MyClass c, int i) {
		System.out.println(c);
		System.out.println(i);
		array(trace).push("4");
	}

	public static void drawStatic(int i, boolean b, Number n) {
		array(trace).push("5");
	}

	public static int drawStatic(String s) {
		array(trace).push("6");
		return s.length();
	}

}

class MyClass {

}
