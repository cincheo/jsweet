package source.nativestructures;

import static jsweet.util.Lang.$export;

import java.util.LinkedList;

public class Lists {

	public static void main(String[] args) {
		testLinkedLists();
	}

	private static void testLinkedLists() {
		LinkedList<String> l = new LinkedList<>();
		l.add("LinkedList___first__value");
		l.add("LinkedList___second__value");
		l.add("LinkedList___third__value");
		String first = l.peekFirst();
		$export("LinkedList_peekFirst", first);
		String last = l.peekLast();
		$export("LinkedList_peekLast", last);
	}
}
