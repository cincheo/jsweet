package source.structural.defaultMethods;

public interface I2 extends I1 {

	default void m2() {
		System.out.println("m2");
	}

	default void o1(String s) {
		System.out.println("o1-s");
	}

	default void o1(Integer i) {
		System.out.println("o1-i");
	}
	
}
