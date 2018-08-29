package source.calculus;

import static def.js.Globals.NaN;
import static jsweet.util.Lang.$export;

public class Numbers {

	public static void main(String[] args) {
		$export("NaN_test", Double.isNaN(NaN));
		float f = 0.3f;
		f = f / 1000000000;
		double d = 0.3;
		d = d / 1000000000;
		assert d != f;
		float f2 = (float) d;
		assert f == f2;
		
		float f3 = 5.0f;
		f3 /= 2;
		$export("Numbers_f3", f3);
	}

}
