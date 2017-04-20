package source.assertion;

import static jsweet.util.Lang.$export;

public class PassingAssertion {

	public static void main(String[] args) {
		int i1 = 1;
		int i2 = 1;

		assert i1 + i2 == 2;
		$export("assertion", true);
	}

}
