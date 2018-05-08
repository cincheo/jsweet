package source.calculus;

import static java.lang.Math.abs;
import static java.lang.Math.cbrt;
import static jsweet.util.Lang.$export;

public class MathApi {

	public static void main(String[] args) {

		// with static import and fully qualified name
		$export("2", java.lang.Math.abs(-1) + abs(-1));
		$export("3", java.lang.Math.cbrt(2));
		$export("4", cbrt(2));

		$export("E", Math.E);
		$export("PI", Math.PI);
		$export("abs_123", Math.abs(-123));
		$export("acos0_1", Math.acos(0.1));
		$export("floor3_3", Math.floor(3.3));
		$export("cbrt2", Math.cbrt(2));
		$export("signum_2342", Math.signum(-2342));
		$export("scalb1_2__2", Math.scalb(1.2, 2));
		$export("toDegres0_5", Math.toDegrees(0.5));

		$export("strict_E", StrictMath.E);
		$export("strict_PI", StrictMath.PI);
		$export("strict_abs_123", StrictMath.abs(-123));
		$export("strict_acos0_1", StrictMath.acos(0.1));
		$export("strict_floor3_3", StrictMath.floor(3.3));
		$export("strict_cbrt2", StrictMath.cbrt(2));
		$export("strict_signum_2342", StrictMath.signum(-2342));
		$export("strict_scalb1_2__2", StrictMath.scalb(1.2, 2));
		$export("strict_toDegres0_5", StrictMath.toDegrees(0.5));

		assert Math.ulp(956.294) == 1.1368683772161603E-13;
		assert Math.ulp(123.1) == 1.4210854715202004E-14;
		
	}

}
