package source.statics;

import static jsweet.util.Lang.$export;

public class StaticInitializer {

	public static void main(String[] args) {
		Globals.Todos.m1();
		$export("result", RESULT);
	}

	static int RESULT;

	static {
		RESULT = m(2);
	}

	static int m(int i) {
		return i * TIMES;
	}

	static int TIMES = 2;

	public void m1() {
	}

}

class Globals {

	public static StaticInitializer Todos = new StaticInitializer();

}
