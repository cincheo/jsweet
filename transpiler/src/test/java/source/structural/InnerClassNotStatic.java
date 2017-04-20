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

public class InnerClassNotStatic {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {
		new InnerClassNotStatic().m();
		int i = InnerClassNotStatic.ActionType.CREATE;
		i = InnerClassNotStatic.ActionType.DELETE;
		i = InnerClassNotStatic.ActionType.UPDATE;
		trace.push("" + i);
		$export("trace", trace.join(","));
	}

	int i = 2;

	int getI() {
		return i;
	}

	private static void staticMethod() {
		trace.push("staticMethod");
	}

	void m() {
		new InnerClassNotStatic.InnerClass1("abc").m1();
		new InnerClass1("ABC").m1();
		new InnerClass2().m();
		new InnerClass1("ABC").m2();
	}

	public class InnerClass1 {
		public static final int I = 2;

		int j = i + 1;

		public InnerClass1(String s) {
			trace.push("" + i + getI() + s);
		}

		public void m1() {
			trace.push("" + i + getI() + "a");
		}

		public void m2() {
			trace.push("test" + InnerClassNotStatic.this.i + InnerClassNotStatic.this.getI() + "a");
			staticMethod();
		}
	}

	public class InnerClass2 {
		public void m() {
			trace.push("" + i + getI() + "b");
			new InnerOfInnerClass().m();
		}

		public final class InnerOfInnerClass {
			public void m() {
				trace.push("" + i + getI() + "c");
			}
		}
	}

	public interface I {
	}

	private class ActionType {
		private static final int CREATE = 0;
		private static final int UPDATE = 1;
		private static final int DELETE = 2;
	}
}

class Sub extends InnerClassNotStatic {
	void m2(InnerClass1 c) {
	}

	void m3(InnerClassNotStatic.InnerClass1 c) {
	}
}

class Use {
	void m2(InnerClassNotStatic.InnerClass1 c) {

	}
}