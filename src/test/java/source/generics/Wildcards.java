package source.generics;

public class Wildcards {

	public void m(I<?> i) {
	}

	public void m2(I<? extends String> i) {
		i.test.indexOf(2);
	}

	public <T extends Wildcards & Comparable<String>> void m3(I<T> i) {
		i.test.compare("a");
	}
	
}

class I<T> {
	T test;
}

interface Comparable<T> {
	int compare(T t);
}