package source.calculus;

import static jsweet.util.Globals.$export;

public class Chars {

	public static void main(String[] args) {
		int j = 5;
		char[] s = new char[1];
		s[0] = (char) (0x0030 + ((int) (j * 10)));
		$export("result", s);
	}

}
