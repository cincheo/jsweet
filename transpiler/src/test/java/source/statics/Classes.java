package source.statics;

import static jsweet.util.Lang.$export;

public class Classes {

	public static void main(String[] args) {
		$export("name1", c1.name);
		$export("name2", c2.name);
	}

	static C c1 = new C();

	static {
		c2 = new C();
	}

	static C c2;

}

class C {

	String name = "name";

}
