package source.init;

import static jsweet.util.Globals.$object;
import jsweet.lang.Object;

public class UntypedObjectWrongUses {

	public static void main(String[] args) {
		Object o1 = $object("a", 1, "b", true, "c");
		$object("a", 1, 2, true);
		$object("a", 1, o1.$get("a"), true, 1, 1);
	}

}
