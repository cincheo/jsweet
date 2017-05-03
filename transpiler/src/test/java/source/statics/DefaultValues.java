package source.statics;

import jsweet.util.Lang;

public class DefaultValues {

	static int i; // initialized to 0
	static boolean b; // to false
	static String s; // initialized to null

	public static void main(String[] args) {
		assert i == 0;
		assert b == false;
		assert Lang.$strict(s == null);
	}

}
