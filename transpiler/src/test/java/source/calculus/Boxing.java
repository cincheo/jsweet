package source.calculus;

import static jsweet.util.Lang.$export;

public class Boxing {

	public static void main(String[] args) {
		double a = 1;
		Double d = new Double(1);
		double b = d.doubleValue();
		if (a == b) {
			$export("unboxedEquals", true);
		}
		
	}

}
