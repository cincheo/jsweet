package source.ambient.testPackage;

import jsweet.lang.Ambient;

public class Globals {

	@Ambient
	public static String globalVariable;

	@Ambient
	public static native String globalFunction(String s);
	
}
