package source.calculus;

import static jsweet.util.Globals.$export;

import jsweet.lang.Date;

public class Longs {

	public static void main(String[] args) {
		Date d = new Date();
		long t1 = (long) d.getTime();
		double t2 = d.getTime();

		$export("t1", t1);
		$export("t2", t2);

	}

}
