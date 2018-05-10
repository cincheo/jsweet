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
package source.api;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.function.Consumer;

import def.js.Array;
import def.js.Promise;

class CC {

}

class CC2 {

}

public class WrongJdkInvocations {

	void m2(Promise<String> p, String s2) {

		p.then(s -> {
			return s.codePointAt(1);
		}).Catch(o -> {
			new Array<String>() //
					.iterator();
		});

		Object o = new def.js.Object() {
			{
				$set("test", s2.codePointAt(1));
			}
		};

		s2.codePointAt(2);

	}

	String s;
	// illegal use of FileInputString
	FileInputStream fis;
	Consumer<String> c;

	void m() {
		toString();
		this.toString();
		s.toString();
		m1().toString();
		s.charAt(1);
		s.concat("");
		s.indexOf("s");
		s.lastIndexOf("gg");
		s.lastIndexOf("", 10);
		// illegal use of codePointAt method
		s.codePointAt(1);
		// Other1 o1 = new Other1();
		// o1.toString();
		// Other2 o2 = new Other2();
		// o2.toString();
		// illegal use of Iterator
		Iterator<String> i = new Array<String>().iterator();
		// illegal use of Iterator method
		i.next();
		c.accept("a");
		// illegal use
		c.andThen(c);
	}

	String m1() {
		s.codePointAt(1);
		return "";
	}

}

class Other1 {
	@Override
	public String toString() {
		// cannot call Object superclass
		return super.toString();
	}

	void m() {
		toString();
		this.toString();
	}
}

class Other2 extends Object {
	void m() {
		toString();
		this.toString();
	}
}

class Other3 {

	void m(Promise<String> p, String s2) {

		p.then(s -> {
			return s.codePointAt(1);
		}).Catch(o -> {
			new Array<String>() //
					.iterator();
		});

		Object o = new def.js.Object() {
			{
				$set("test", s2.codePointAt(1));
			}
		};

		s2.codePointAt(2);

	}

}