package source.init;

import static jsweet.util.Lang.$export;

import def.js.Array;

public class Cloning {

	static Array<String> trace = new Array<>();

	public static void main(String[] args) {

		C2 c2 = new C2();
		trace.push("" + c2.i);
		trace.push("" + c2.j);
		try {
			c2 = (C2) c2.clone();
			trace.push("" + c2.i);
			trace.push("" + c2.j);

			C3 c3 = new C3(5);
			trace.push("" + c3.i);
			trace.push("" + c3.j);
			c3 = (C3) c3.clone();
			trace.push("" + c3.i);
			trace.push("" + c3.j);

			C4 c4 = new C4();
			C4.clone(c4);

		} catch (Exception e) {
			// will fail the test
		}

		$export("result", trace.join(","));

	}

}

class C1 implements Cloneable {

	public int i = 1;

	@Override
	protected Object clone() throws CloneNotSupportedException {
		C1 clone = (C1) super.clone();
		clone.i = i + 1;
		return clone;
	}

}

class C2 extends C1 implements Cloneable {

	public int j = 2;

}

class C3 extends C1 implements Cloneable {

	public int j = 2;

	public C3(int j) {
		this.j = j;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		C3 clone = (C3) super.clone();
		clone.j = j + 1;
		return clone;
	}

}

class C4 extends C1 implements Cloneable {

	public static C4 clone(C4 c4) {
		return c4;
	}

	public int j = 2;

}
