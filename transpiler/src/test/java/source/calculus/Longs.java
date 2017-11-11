package source.calculus;

import static jsweet.util.Lang.$export;

import def.js.Date;

public class Longs {

	public static void main(String[] args) {
		Date d = new Date();
		long t1 = (long) d.getTime();
		double t2 = d.getTime();

		$export("t1", t1);
		$export("t2", t2);

		long l = 1L;
		$export("l", l / 2);

		$export("c", Long.compare(1, 2));

		assert (long)1.2 == 1;
		assert (long)-1.2 == -1;
		
	}

}
