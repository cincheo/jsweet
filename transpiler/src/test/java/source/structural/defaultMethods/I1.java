package source.structural.defaultMethods;

public interface I1 {

	default void m1() {
		System.out.println("m1");
	}

	void m();

}