package source.transpiler;

import static def.js.Globals.eval;

public class Extended extends def.js.Object {

	void testMethod() {
	}

	public static void main(String[] args) {
		Extended extended = new Extended();
		// JSweet extension erases all these elements
		assert extended.$get("testMethod") == null;
		assert eval("source.transpiler.AClass") == null;
		assert eval("source.transpiler.p") == null;
	}

}

class AClass {

}
