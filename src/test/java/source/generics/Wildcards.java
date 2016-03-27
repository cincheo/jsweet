package source.generics;

public class Wildcards {

	public void m(I<?> i) {
	}

	public void m2(I<? extends String> i) {
		i.test.indexOf('2');
	}

	public <T extends Wildcards & Comparable<String>> void m3(I<T> i) {
		i.test.compare("a");
	}
	
	public static <T extends Object & Comparable<? super T>> T max(Collection<? extends T> coll) {
		return (T)Wildcards.max2(coll, null);
	}

	public static <T> T max2(Collection<? extends T> coll, Comparator<? super T> comp) {
		return coll.next();
	}
	
	
	public static void main(String[] args) {
		Wildcards.max(null);
	}
	
}

class I<T> {
	T test;
}

interface Comparable<T> {
	int compare(T t);
}

interface Collection<T> {
	T next();
}

interface Comparator<T> {
	int compare(T t1, T t2);
}

