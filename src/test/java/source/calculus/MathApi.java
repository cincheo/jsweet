package source.calculus;

import static jsweet.util.Globals.$export;

public class MathApi {

	public static void main(String[] args) {
		
		$export("E", Math.E);
		$export("PI", Math.PI);
		$export("abs_123", Math.abs(-123));
		$export("acos0_1", Math.acos(0.1));
		$export("floor3_3", Math.floor(3.3));
		$export("cbrt2", Math.cbrt(2));
		$export("signum_2342", Math.signum(-2342));
		$export("scalb1_2__2", Math.scalb(1.2, 2));
		
	}
	
}
