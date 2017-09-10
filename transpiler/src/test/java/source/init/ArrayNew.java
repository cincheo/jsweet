package source.init;

import static jsweet.util.Lang.$export;
import static jsweet.util.Lang.array;

import def.js.Array;

public class ArrayNew {

	public static void main(String[] args) {
		int[] newArray = new int[10];
		String s = "";
		for (int i = 0; i < newArray.length; i++) {
			s += newArray[i] + ",";
		}
		$export("result", s);

		String[] strings1 = { "a", "b", "c" };
		assert strings1[1] == "b";

		String[][] strings = new String[2][2];
		assert strings.length == 2;
		assert strings[0].length == 2;
		strings[0][0] = "a";
		assert strings[0][0] == "a";

		strings1 = new String[] { "a", "b", "c" };
		assert strings1.length == 3;
		array(strings1).push("d");
		assert (int) strings1.length == 4;
		assert strings1[3] == "d";

		Array<String> strings3 = array(new String[] { "a", "b", "c" });
		assert strings3.length == 3;
		strings3.push("d");
		assert (int) strings3.length == 4;
		assert strings3.$get(3) == "d";

		strings3 = new Array<String>("a", "b", "c");
		assert strings3.length == 3;
		strings3.push("d");
		assert (int) strings3.length == 4;
		assert strings3.$get(3) == "d";

		strings3 = new Array<String>();
		strings3.push("a", "b", "c");
		assert strings3.length == 3;
		strings3.push("d");
		assert (int) strings3.length == 4;
		assert strings3.$get(3) == "d";

		boolean[] thing = new boolean[5];
		for (int i = 0; i < 5; i++) {
			thing[i] = false;
			assert thing[i] == false;
		}

		boolean[] thing2 = new boolean[5];
		for (int i = 0; i < 5; i++) {
			assert thing2[i] == false;
		}

		Wrap w = new Wrap();
		assert w.b == false;
	}

}

class Wrap {
	boolean b; // defaults to false correctly
}
