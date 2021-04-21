package source.varargs;

import static jsweet.util.Lang.$export;

public class VarargsAndOverloads {

	public static void main(String[] args) {
		VarargsAndOverloads test = new VarargsAndOverloads();
		$export("1", test.m1(2.1));
		$export("2", test.m1(2.1, "a", "b"));
		$export("3", test.m1("a"));
		
		$export("4", test.m2(2.1));
		$export("5", test.m2(2.1, "a"));
		$export("6", test.m2("a"));
		$export("7", test.m2("a", "b", "c"));
	}	
	
	public String m1(double p1, String... p2) {
		return ""+p1+"-"+p2;
	}

	public String m1(double p1) {
		return ""+p1;
	}

	public String m1(String p1) {
		return ""+p1;
	}

	public String m2(double p1, String p2) {
		return ""+p1+"-"+p2;
	}

	public String m2(double p1) {
		return ""+p1;
	}

	public String m2(String... p1) {
		return ""+p1;
	}
		
}
