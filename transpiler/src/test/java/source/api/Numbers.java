package source.api;

import static jsweet.util.Lang.$export;

import def.js.Array;

public class Numbers {

	static Array<String> trace = new Array<String>();

	public static void main(String[] args) {
		
		Integer i = parseDuration("1:20");
		assert i == 80;

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
