package source.init;

import static jsweet.util.Globals.$export;

public class ArrayNew {

	public static void main(String[] args) {
		int[] newArray = new int[10];
		String s = "";
		for (int i = 0; i < newArray.length; i++) {
			s += ".";
		}
		$export("result", s);
	}

}
