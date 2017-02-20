package source.transpiler;

public class PrefixExtension {

	public static void main(String[] args) {
		API api = new API();
		assert api.$get("s1") == "s1";
		assert api.$get("__s2") == "s2";
		assert api.$get("m2") == null;
		assert api.$get("__m2") != null;
		assert api.m() == "s1s2";
	}

}

class API extends def.js.Object {

	public String m() {
		return m2();
	}

	public String s1 = "s1";

	String s2 = "s2";

	private String m2() {
		return s1 + s2;
	}

}
