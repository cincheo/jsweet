package source.structural.wrongglobals;

import static jsweet.util.Lang.object;

public class Globals extends SuperClass {

	public Globals() {
	}
	
	public void m() {
	}

	public boolean b = true;

	public static void staticM() {
		object(B).$get("p");
	}
	
	public static boolean B = true;
	
}

class SuperClass {
	
}

