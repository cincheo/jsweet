package source.structural;

import static jsweet.util.Lang.$export;

public class NoNameClashesWithFields {

	private int size = 2;

	public int size() {
		return size;
	}

	public int size(int i) {
		return this.size + i;
	}

	public String field = "hello";

	public String field() {
		return field;
	}

	public static void main(String[] args) {
		NoNameClashesWithFields o = new NoNameClashesWithFields();
		$export("v1", o.size);
		$export("v2", o.size());
		$export("v3", o.size(1));
		$export("v4", OtherClass.field(o));
		$export("v5", OtherClass.field2(o));
	}

}

class OtherClass {
	static String field(NoNameClashesWithFields o) {
		return o.field;
	}

	static String field2(NoNameClashesWithFields o) {
		return o.field();
	}

}
