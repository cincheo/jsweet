package source.overload;

import static jsweet.util.Lang.$export;
import def.js.Array;

class ASuperClass {

}

public class ConstructorOverloadWithFieldInitializer extends ASuperClass {

	static Array<String> trace = new Array<String>();

	public static void main(String[] args) {
		new ConstructorOverloadWithFieldInitializer("other");
		new ConstructorOverloadWithFieldInitializer(2);
		$export("trace", trace.join(","));
	}

	String s = "test";
	int i = 1;
	boolean m = true;

	public ConstructorOverloadWithFieldInitializer() {
		super();
	}

	public ConstructorOverloadWithFieldInitializer(String s) {
		trace.push(this.s);
		this.s = s;
	}

	public ConstructorOverloadWithFieldInitializer(int i) {
		super();
		trace.push("" + this.i);
		this.i = i;
	}

	public boolean m() {
		return true;
	}

}
