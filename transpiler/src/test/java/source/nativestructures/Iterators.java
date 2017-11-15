package source.nativestructures;

import java.util.Iterator;

public class Iterators {

	public static void main(String[] args) {
		Range r = new Range(10, 20);
		Iterator<Integer> it = r.iterator();
		int j = 10;
		while (it.hasNext()) {
			int i = it.next();
			assert i == j++;
		}
		assert j == 20;
		
		r = new Range(10, 20);
		j = 10;
		for (Integer i : r) {
			assert i == j++;
		}
		assert j == 20;		
	}

}

class Range implements Iterable<Integer> {

	private int start;
	private int end;
	private int at;

	public Range(int start, int end) {
		this.start = start;
		this.end = end;
		at = start;
	}

	public Iterator<Integer> iterator() {
		return new RangeIterator();
	}

	private class RangeIterator implements Iterator<Integer> {

		public boolean hasNext() {
			return at < end;
		}

		public Integer next() {
			int next = at;
			at += 1;
			return next;
		}

		public void remove() {
		}
	}

}