package source.api;

import static jsweet.lang.JSON.stringify;
import jsweet.lang.JSON;
import static jsweet.util.Globals.$get;
import jsweet.util.Globals;

public class AccessStaticMethod {

	public static void main(String[] args) {
		JSON.stringify("test");
		jsweet.lang.JSON.stringify("test");
		stringify("test");
		Object o = null;
		$get(o, "f");
		Globals.$get(o, "f");
		jsweet.util.Globals.$get(o, "f");
	}

}
