package source.overload;

import static jsweet.util.Lang.$export;

import def.js.Array;

enum MyEnum {
	A, B
}

public class OverloadWithEnums {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {
		new OverloadWithEnums().m("a");
		new OverloadWithEnums(MyEnum.A).m(MyEnum.B);
		$export("trace", trace.join(","));
	}

	public OverloadWithEnums() {
		trace.push("1");
	}

	public OverloadWithEnums(MyEnum e) {
		trace.push("3");
	}

	public void m(String s) {
		trace.push("2");
	}

	public void m(MyEnum e) {
		trace.push("4");
	}

}
