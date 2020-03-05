package source.structural.defaultMethods;

public interface I extends J {

	String m1();

	default String m2() {
		return "m2" + m3();
	}

	String m3();

}
