package source.init;

import static jsweet.util.Lang.$map;

import def.js.Object;

public class UntypedObjectWrongUses {

	public static void main(String[] args) {
		Object o1 = $map("a", 1, "b", true, "c");
		$map("a", 1, 2, true);
		$map("a", 1, o1.$get("a"), true, 1, 1);
	}

}
