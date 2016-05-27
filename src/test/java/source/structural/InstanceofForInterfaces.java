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

import java.util.function.BinaryOperator;
import java.util.function.IntFunction;

import jsweet.lang.Array;
import jsweet.lang.Interface;
import jsweet.lang.Optional;

public class InstanceofForInterfaces {

	static Array<String> trace = new Array<>();

	void m(Object o) {
		if (o instanceof I1) {
			@SuppressWarnings("unused")
			I1 i1 = (I1) o;
			trace.push("1");
		}
		if (o instanceof I2) {
			trace.push("2");
		}
		if (o instanceof C3) {
			trace.push("3");
		}
	}

	static void m2(BinaryOperator<String> op) {
		assert op instanceof BinaryOperator && !(op instanceof IntFunction);
	}

	public static void main(String[] args) {
		I1 i1 = new I1() {
		};
		new InstanceofForInterfaces().m(i1);
		I2 i2 = new I2() {
			{
				s = "s";
			}
		};
		new InstanceofForInterfaces().m(i2);
		new InstanceofForInterfaces().m(new C3());
		m2((a, b) -> a + b);
		$export("trace", trace.join());
	}

}

interface I1 {
}

@Interface
abstract class I2 {
	@Optional
	int f;
	String s;
}

class C3 implements I1 {
	int i;
	// C3() {
	// i = 2;
	// }
}
