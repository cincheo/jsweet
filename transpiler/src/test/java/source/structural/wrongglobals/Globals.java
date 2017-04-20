package source.structural.wrongglobals;

import static jsweet.util.Lang.$get;

public class Globals extends SuperClass {

	public Globals() {
	}
	
	public void m() {
	}

	public boolean b = true;

	public static void staticM() {
		$get(B, "p");
	}
	
	public static boolean B = true;
	
}

class SuperClass {
	
}

