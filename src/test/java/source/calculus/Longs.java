package source.calculus;

import static jsweet.util.Globals.$export;

import jsweet.lang.Date;

public class Longs {

	public static void main(String[] args) {
		long t1 = (long)new Date().getTime();
		double t2 = new Date().getTime();
		
		$export("t1", t1);
		$export("t2", t2);
		
	}

}
