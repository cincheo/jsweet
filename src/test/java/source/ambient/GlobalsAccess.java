package source.ambient;

import static source.ambient.three.Globals.globalVariable;
import static source.ambient.three.Globals.globalFunction;

public class GlobalsAccess {

	public static void main(String[] args) {
		String s = globalVariable;
		globalFunction(s);
	}

}
