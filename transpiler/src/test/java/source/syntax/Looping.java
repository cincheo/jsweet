package source.syntax;

interface Collection<T> {
	Iterator<T> iterator();
}

interface Comparator<T> {
	int compare(T t1, T t2);
}

interface Iterator<T> {
	boolean hasNext();

	T next();
}

interface Comparable<T> {

}

class Comparators {
	public static <T> Comparator<T> natural() {
		return null;
	};
}

public class Looping {

	String a = "a", b = "b";

	public static void main(String[] args) {
		int ii =0, jj=0;
		for (int i = 0, j = 0; i < 100; i++, j++) {
			System.out.println(i + j);
			ii=i;
			jj = j;
		}
		assert ii==99;
		assert jj==99;
		int i = 10;
		do {
			System.out.println(i);
		} while (i-- > 0);
		System.out.println(">"+i);
		assert i==-1;
		i=10;
		do
			System.out.println(i);
		while (i-- > 0);
		System.out.println(">"+i);
		assert i==-1;
		i=10;
		while (i-- > 0) {
			System.out.println(i);
		}
		System.out.println(">"+i);
		assert i==-1;
		i=10;
		while (i-- > 0)
			System.out.println(i);
		System.out.println(">"+i);
		assert i==-1;

	}

	public static <T extends Object & Comparable<? super T>> T max(Collection<? extends T> coll) {
		Collection<String> c = null;
		// TODO: Java accepts this assignment without casting but TypeScript
		// does not
		// RP: I think that Java is wrong and TypeScript is right...
		@SuppressWarnings("unused")
		String s = (String) Looping.max(c, null);
		return (T) Looping.max(coll, null);
	}

	public static <T> T max(Collection<? extends T> coll, Comparator<? super T> comp) {

		if (comp == null) {
			comp = Comparators.natural();
		}

		Iterator<? extends T> it = coll.iterator();

		// Will throw NoSuchElementException if coll is empty.
		T max = it.next();

		while (it.hasNext()) {
			T t = it.next();
			if (comp.compare(t, max) > 0) {
				max = t;
			}
		}

		return max;
	}

}
