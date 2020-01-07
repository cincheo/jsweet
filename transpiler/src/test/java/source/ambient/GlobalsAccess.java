package source.ambient;

import static source.ambient.testPackage.Globals.globalFunction;
import static source.ambient.testPackage.Globals.globalVariable;

public class GlobalsAccess {

	public static void main(String[] args) {
		String s = globalVariable;
		globalFunction(s);
	}

}
