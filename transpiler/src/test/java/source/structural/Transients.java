package source.structural;

import static jsweet.util.Lang.$export;

import def.js.Array;
import jsweet.util.Lang;

public class Transients {

	public static void main(String[] args) {
	    Array<String> keys = Lang.$insert("Object.keys(new ChildWithTransients())");
	    System.out.println("keys = "+keys);
		$export("keys", keys.join(","));
	}

}

class ParentWithTransients {
	int a;
    transient int b;
}

class ChildWithTransients extends ParentWithTransients {
    int c;
    transient int d;

}