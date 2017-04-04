package source.debug;

public class Simple {

	String field;

	public Simple() {
		field = "toto";
		field = "toto2";
	}
	
	void m1(String s) {
		m2(s);
	}

	void m2(String s) {
		if (s != null) {
			this.field = s;
		} else {
			this.field = s + "3";
		}
	}

	String m3(String s) {
		this.field = s;
		return this.field;
	}

	public static void main(String[] args) {
		Simple s = new Simple();
		s.m1("test");
		s.m2("test");
	}

}
