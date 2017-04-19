package source.debug;

import static def.dom.Globals.console;

public class Simple {

	String field;

	public Simple() {
		field = "toto";
		field = "toto2";
	}
	
	boolean m1(String s) {
		m2(s);
		for(int i=0;i<s.length();i++) {
			System.out.println(i);
		}
		return false;
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
		boolean b = s.m1("test");
		s.m2("test");
		console.info("abc");
		String str = s.m3("abc");
	}

}
