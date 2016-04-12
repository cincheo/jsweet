package source.generics;

public class AddThisOnGenericMethods<T> {

	T t;
	
	private <U> void m(AddThisOnGenericMethods<U> i) {
	}

	public <U> void m2(AddThisOnGenericMethods<U> i) {
		m(i);
		m7(t);
		m8(t);
		m9(3);
	}

	public void m3(AddThisOnGenericMethods<String> i) {
		m(i);
	}
	
	public void m4(AddThisOnGenericMethods<String> i) {
		m3(i);
	}

	public void m5(AddThisOnGenericMethods<T> i) {
	}
	
	public void m6(AddThisOnGenericMethods<T> i) {
		m5(i);
	}

	public void m7(T i) {
	}
	
	public <U> void m8(U i) {
	}
	
	public void m9(Number n) {
	}
	
	
}
