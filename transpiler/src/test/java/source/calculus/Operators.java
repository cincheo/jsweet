package source.calculus;

public class Operators {

	public static void main(String[] args) {
		boolean b1 = true;
		boolean b2 = false;

		boolean b3 = b1 | b2;
		assert b3;
		b3 &= b1;
		// assert b3 == true;
		b1 |= b3;
		// assert b1;

		b1 = b1 ^ b2;
		assert b1;

		int i1 = 1;
		int i2 = 3;
		int i3 = i1 | i2;
		i1 &= i3;

		assert xor(true, false);
		assert !xor(true, true);

		testWithObjects();
	}

	public static void testWithObjects() {
		Boolean b1 = true;
		Boolean b2 = false;

		Boolean b3 = b1 | b2;
		assert b3;
		b3 &= b1;
		// assert b3 == true;
		b1 |= b3;
		// assert b1;

		b1 = b1 ^ b2;
		assert b1;

	}

	public static Boolean xor(Boolean x, Boolean y) {
		return x ^ y;
	}

}
