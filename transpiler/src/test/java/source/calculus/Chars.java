package source.calculus;

import static jsweet.util.Globals.$export;

public class Chars {

	public static void main(String[] args) {
		int i = 'i';
		long l = 'i';
		byte b = 'i';
		short sh = 'i';
		double d = 'i';
		float f = 'i';
		f = 'f';
		char c1 = (char) 0;
		int ii = (int) 'a';
		char c3 = 0;
		int j = 5;
		char[] s = new char[1];
		s[0] = (char) (0x0030 + ((int) (j * 10)));
		$export("result", s);
		char c2 = (char) test2();
		test((char) 0);
		// char expression -> int
		int i3 = c1;
		test3('a');
		test3(c1);
		f = c1;
		"abc".lastIndexOf('a');
		"abc".indexOf('a');
		// would need latest j4ts
		// Character.toUpperCase(i3);

		char c = 'a';
		if (c == 60) {
			System.out.println("success");
		}

		int i4 = 20;
		switch (i4) {
		case 12:
		case 'a':
		case 'b':
		}
		boolean test = true;
		String string = "abc";
		int result = (test) ? -1 : string.charAt(0);
		result = string.charAt(0);
		result = (test) ? m(test ? 'a' : string.charAt(0)) : string.charAt(0);
		
		byte[] data = {};
		if (data[2] == '\n') {}

	}

	public static final int MIN_RADIX = 2;

	public static final int MAX_RADIX = 36;

	public static void test(char c) {
	}

	public static void test3(int c) {
	}

	public static int test2() {
		return 0;
	}

	static int m(char i) {
		return (char) i;
	}

	public static int digit(char c, int radix) {
		if (radix < MIN_RADIX || radix > MAX_RADIX) {
			return -1;
		}

		if (c >= '0' && c < '0' + Math.min(radix, 10)) {
			return c - '0';
		}

		// The offset by 10 is to re-base the alpha values
		if (c >= 'a' && c < (radix + 'a' - 10)) {
			return c - 'a' + 10;
		}

		if (c >= 'A' && c < (radix + 'A' - 10)) {
			return c - 'A' + 10;
		}

		return -1;
	}

	public static int getChar(char c) {
		return c;
	}

}
