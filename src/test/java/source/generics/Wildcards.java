package source.generics;

public class Wildcards {

	public void m(I<?> i) {
	}

	public void m2(I<? extends String> i) {
		i.test.indexOf(2);
	}
	
}

class I<T> {
	T test;
}
