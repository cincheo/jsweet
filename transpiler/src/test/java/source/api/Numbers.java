package source.api;

import static jsweet.util.Lang.$export;
import static jsweet.util.Lang.number;
import static jsweet.util.Lang.string;

import def.js.Array;

public class Numbers {

	static Array<String> trace = new Array<String>();

	public static void main(String[] args) {
		
		Integer i = parseDuration("1:20");
		assert i == 80;

		float f = Float.intBitsToFloat(Float.floatToIntBits(3.14f));
		assert f!=3.14f;
		assert number(f).toFixed(2) == "3.14";
		
		double d = Double.longBitsToDouble(Double.doubleToLongBits(3.14));
		assert d!=3.14;
		assert ((double)Math.round(d*100))/100 == 3.14;
		
		$export("trace", trace.join(","));
	}

	public static Integer parseDuration(String duration) {
		if (duration == null) {
			return null;
		}
		String[] parts = duration.split(":");
		switch (parts.length) {
		case 1:
			return new Integer(parts[0]) * 60;
		case 2:
			return new Integer(parts[0]) * 60 + new Integer(parts[1]);
		}
		return null;
	}
	
}
