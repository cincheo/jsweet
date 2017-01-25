package source.structural;

import static jsweet.util.Globals.$export;

import jsweet.lang.TypeScriptBody;

public class TypeScriptBodyAnnotation {

	public static void main(String[] args) {
		$export("test1", new TypeScriptBodyAnnotation().m1());
		$export("test2", new TypeScriptBodyAnnotation().m2());
		$export("test3", new TypeScriptBodyAnnotation().m3());
	}

	int i = 1;

	@TypeScriptBody("return this.i + 1;")
	public int m1() {
		return i;
	}

	public int m2() {
		return i;
	}

	public int m3() {
		return i;
	}

}
