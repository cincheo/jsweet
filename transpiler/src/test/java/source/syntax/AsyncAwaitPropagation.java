package source.syntax;

import jsweet.lang.Async;

public class AsyncAwaitPropagation {

	@Async int m1() {
		return 0;
	}

	int m2() {
		return 1 + m1();
	}

	void m3() {
		if (m1() == 1) {
			// do something
		} else {
			// do something else
		}
	}

	int m4() {
		return m2();
	}

}