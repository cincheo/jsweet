package source.init;

import static jsweet.util.Globals.$export;
import static jsweet.util.Globals.$map;

import def.js.Object;

public class UntypedObject {

	public static void main(String[] args) {
		Object o = $map("a", 1, "b", true);
		$export("a", o.$get("a"));
		$export("b", o.$get("b"));
	}

}
