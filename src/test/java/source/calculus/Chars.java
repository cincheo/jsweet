package source.calculus;

import static jsweet.util.Globals.$export;

public class Chars {

	public static void main(String[] args) {
		int j = 5;
		char[] s = new char[1];
		s[0] = (char) (0x0030 + ((int) (j * 10)));
		$export("result", s);
	}
	
	public static final int MIN_RADIX = 2;

	public static final int MAX_RADIX = 36;
	
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
