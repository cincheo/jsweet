package source.ambient;

import static source.ambient.three.Globals.globalFunction;
import static source.ambient.three.Globals.globalVariable;

public class GlobalsAccess {

	public static void main(String[] args) {
		String s = globalVariable;
		globalFunction(s);
	}

}
