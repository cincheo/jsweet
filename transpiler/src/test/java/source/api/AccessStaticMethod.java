package source.api;

import static def.js.JSON.stringify;
import def.js.JSON;
import static jsweet.util.Lang.object;

public class AccessStaticMethod {

	public static void main(String[] args) {
		JSON.stringify("test");
		def.js.JSON.stringify("test");
		stringify("test");
		Object o = null;
		// deprecated
		// $get(o, "f");
		// Globals.$get(o, "f");
		// jsweet.util.Globals.$get(o, "f");

		// non-deprecated way
		object(o).$get("f");

	}

}
