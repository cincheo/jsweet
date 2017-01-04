package source.calculus;

public class Null {

	static String s3;
	static String s4 = null;

	public static void main(String[] args) {
		String s1 = "a";
		String s2 = null;
		assert s1 != null;
		assert s2 == null;
		assert m1() == null;
		assert !(m2() != null);
		assert s3 == null;
		assert s4 == null;
		assert null == null;
	}

	static String m1() {
		return null;
	}

	static String m2() {
		return s3;
	}

}
