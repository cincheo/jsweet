package source.statics;

import static jsweet.util.Lang.$export;

public class StaticsInInterfaces {

	public static void main(String[] args) {
		$export("c1", Foo.CONSTANT_TEST);
		$export("c2", Foo.CONSTANT_TEST2);
	}

}

interface Foo {

	int CONSTANT_TEST = 1;
	int CONSTANT_TEST2 = 2;

	void setSomethingInFooImpl();

}
