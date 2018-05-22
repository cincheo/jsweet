package source.calculus;

import static jsweet.util.Lang.$export;

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

		byte b2 = '\n';
		byte[] data = { 'a', 'b', '\n' };
		if (data[2] == '\n') {
		}
		assert data[2] == '\n';

		Foo foo = new Foo();
		foo.bar("baz", 2);

		char c4 = 'A';
		char c5 = c4;
		c4 += '0';
		assert c4 != c5;
		c4 -= '0';
		assert c4 == c5;
		c4 *= '0';
		c4 /= '0';

		char x = 'a';
		x = (char) ((int) x & 0x20);
		x &= 0x20;
		// System.out.println("Char: " + x);

		char[] buf = new char[2];
		assert buf.length == 2;
		"abcd".getChars(1, 3, buf, 0);
		assert buf.length == 2;
		assert buf[0] == 'b';
		assert buf[1] == 'c';

		int[] testArray = { 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120 };
		testArray['\n'] = 50;
		assert testArray[10] == 50;

		char baseChar = 'X';
		String concatResult = "test" + baseChar;
		System.out.println(concatResult);
		assert concatResult == "testX";

		int addResult = 2 + baseChar;
		System.out.println(addResult);
		assert addResult == 90;

		char idxChar = 2;
		int[] array = { 1, 2, 3 };
		System.out.println("checkIdx" + array[idxChar]);
		assert array[idxChar] == 3;
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

class Foo {
	final int EOF = -1;
	final char ETX = 3;
	final char _CR = 0xD;
	final char _LF = 0xA;
	final char _BS = 0x8;
	final char _HT = 0x9;
	final char _VT = 0xB;
	final char _SO = 0xE;
	final char _SI = 'a';
	final int ESC = 0x1B;
	final int NUMPENS = 256;
	final int LT_solid = 0;
	final int LT_adaptive = 1;
	final int LT_plot_at = 2;
	final int LT_fixed = 3;
	final int LT_MIN = -8;
	final int LT_ZERO = 0;
	final int LT_MAX = 8;
	final int LB_direct = 0;
	final int LB_buffered = 1;
	final int LB_buffered_in_use = 2;
	final String TEST = "abc";

	void bar(String txt, int mode) {
		txt = txt + "\u0000";
		int i;
		char c;

		for (i = 0; (c = txt.charAt(i)) != '\0'; ++i) {
			switch (c) {
			case ' ':
				break;
			case _CR:
				switch (mode) {
				case LB_direct:
					break;
				case LB_buffered:
					break;
				default:
					break;
				}
				break;
			case _LF:
				break;
			case _BS:
				break;
			case _HT:
				break;
			case _VT:
				break;
			case _SO:
				break;
			case _SI:
				break;
			default:
				break;
			}
			switch (txt) {
			case "ABC":
			case TEST:
				break;
			default:
				break;
			}
			switch (mode) {
			case 1:
			case _LF:
			}
		}
	}
}