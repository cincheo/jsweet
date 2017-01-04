package source.calculus;

public class Operators {

	public static void main(String[] args) {
		boolean b1 = true;
		boolean b2 = false;

		boolean b3 = b1 | b2;
		b3 &= b1;
		b1 |= b3;

		b1 = b1 ^ b2;

		int i1 = 1;
		int i2 = 3;
		int i3 = i1 | i2;
		i1 &= i3;

	}

}
