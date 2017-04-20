package source.api;

import static def.js.JSON.stringify;
import def.js.JSON;
import static jsweet.util.Lang.$get;
import jsweet.util.Lang;

public class AccessStaticMethod {

	public static void main(String[] args) {
		JSON.stringify("test");
		def.js.JSON.stringify("test");
		stringify("test");
		Object o = null;
		$get(o, "f");
		Lang.$get(o, "f");
		jsweet.util.Lang.$get(o, "f");
	}

}
