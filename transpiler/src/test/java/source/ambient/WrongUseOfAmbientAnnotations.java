package source.ambient;

import jsweet.lang.Ambient;

public class WrongUseOfAmbientAnnotations {

	@Ambient
	public void m() {
	}

	@Ambient
	public static String v;
	
}
