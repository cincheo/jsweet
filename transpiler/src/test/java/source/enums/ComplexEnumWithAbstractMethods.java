package source.enums;

import static jsweet.util.Lang.$export;

import def.js.Array;

public class ComplexEnumWithAbstractMethods {
	static Array<String> trace = new Array<String>();

	public static void main(String[] args) {
		trace.push("" + MyAbstractEnum.A.m());
		trace.push("" + MyAbstractEnum.B.m());
		$export("trace", ">" + trace.join(","));
	}
}

enum MyAbstractEnum {
	A {
		@Override
		public String m() {
			class Test {
				String s;
				Test(String s) {
					this.s = s;
				}
			}
			return new Test("ok1").s;
		}
	},
	B {
		private String a = "ok2";

		@Override
		public String m() {
			return a;
		}
	};

	public abstract String m();

}