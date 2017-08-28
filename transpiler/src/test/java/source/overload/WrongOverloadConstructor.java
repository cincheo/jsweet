package source.overload;

import static jsweet.util.Lang.$export;

import def.js.Array;

public class WrongOverloadConstructor {

	public static Array<String> trace = new Array<>();

	public static void main(String[] args) {
		WrongOverloadConstructor c = new WrongOverloadConstructor();
		c.new Floaty1(24, 7);
		c.new Floaty1(6);
		c.new Floaty1(true);
		new Floaty2(24, 7);
		new Floaty2(6);
		new Floaty2(true);
		new Floaty3(24, 7);
		new Floaty3(6);
		new Floaty3(true);
		$export("trace", trace.join(":"));
	}

	class Floaty1 {
		Floaty1(float foo, float bar) {
			WrongOverloadConstructor.trace.push("1.1(" + foo + "," + bar + ")");
			assert foo == 24;
			assert bar == 7;
		}

		Floaty1(float foo) {
			WrongOverloadConstructor.trace.push("1.2(" + foo + ")");
			assert foo == 6;
		}

		Floaty1(boolean baz) {
			WrongOverloadConstructor.trace.push("1.3(" + baz + ")");
			assert baz == true;
		}
	}

	
}

class Floaty2 {
	Floaty2(float foo, float bar) {
		WrongOverloadConstructor.trace.push("2.1(" + foo + "," + bar + ")");
		assert foo == 24;
		assert bar == 7;
	}

	Floaty2(float baz) {
		WrongOverloadConstructor.trace.push("2.2(" + baz + ")");
		assert baz == 6;
		assert baz + 1 == 7;
	}

	Floaty2(boolean baz) {
		WrongOverloadConstructor.trace.push("2.3(" + baz + ")");
		assert baz == true;
	}
}

class Floaty3 {
	Floaty3(float foo, float bar) {
		WrongOverloadConstructor.trace.push("3.1(" + foo + "," + bar + ")");
		assert foo == 24;
		assert bar == 7;
	}

	Floaty3(float bar) {
		WrongOverloadConstructor.trace.push("3.2(" + bar + ")");
		assert bar == 6;
		assert bar + 1 == 7;
	}

	Floaty3(boolean foo) {
		WrongOverloadConstructor.trace.push("3.3(" + foo + ")");
		assert foo == true;
	}
}

