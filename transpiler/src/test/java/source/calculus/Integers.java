package source.calculus;

import static jsweet.util.Lang.$export;

public class Integers {

	public static void main(String[] args) {
		int i = 3;
		double f = 3;
		$export("i", i);
		$export("i1", i / 2);
		$export("i2", 3 / 2);
		$export("f1", f / 2);
		$export("f2", 3.0 / 2);
		$export("f3", i * 2.5);
		$export("i3", (int) (f * 2.5));
		$export("i4", 1 + (int) (f * 2.5) - 1);
		String[] l = { "a", "b" };
		if (i < 2) {

		}
		for (int i2 = 0; i2 < l.length; i2++) {
			System.out.println(l[i2]);
		}
		
		
		int j = 5;
		j /= 3;
		System.out.println(j);
		$export("j", j);
		
	}

}
