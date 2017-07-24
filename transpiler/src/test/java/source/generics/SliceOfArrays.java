package source.generics;

import def.js.Array;

public class SliceOfArrays<E> {

	class ArrayEnumeration<E> {
		private Array<E> a;
		private int s;
		public ArrayEnumeration (Array<E> a, int s) {
		}
		public void print () {
			System.out.println(a.$get(0));
		}
	}

	private Array<E> arr;
	private int size;

	public SliceOfArrays () {
		arr = new Array<>();
		size++;
	}

	public void add (E e) {
		arr.push(e);
	}

	public void demo () {
		ArrayEnumeration<E> ae = elements();
		ae.print();
	}

	public ArrayEnumeration<E> elements () {
		return new ArrayEnumeration<E>(arr.slice(), size);
	}

	public static void main () {
		SliceOfArrays v = new SliceOfArrays<String>();
		v.add("Hello, world");
		v.demo();
	}

}