package source.overload;

import java.util.Iterator;

public class InterfaceInheritance {

	public static void main(String[] args) {
		Collection2<String> l = new ArrayList<>();
		l.add("a");
		l.addAll(new ArrayList<>());
		l.clear();
		l.contains("");
		l.containsAll(new ArrayList<>());
		l.equals("");
		l.hashCode();
		l.isEmpty();
		l.remove("");
		l.removeAll(new ArrayList<>());
		l.retainAll(new ArrayList<>());
		l.size();
		l.toArray();
		l.toArray(new String[0]);
		String o = max(new ArrayList<>(), null);
	}

	public static <T> T max(Collection2<? extends T> coll, Queue2<? super T> comp) {
		return null;
	}

	public static <T> T max(Collection2<? extends T> coll) {
		return null;
	}

}

// TODO: we cannot implement iterable here because it contains a default method
// defined in J4TS (we should support default method without inlining)
interface Collection2<E> { // extends Iterable<E> {

	boolean add(E o);

	boolean addAll(Collection2<? extends E> c);

	void clear();

	boolean contains(Object o);

	boolean containsAll(Collection2<?> c);

	@Override
	boolean equals(Object o);

	@Override
	int hashCode();

	boolean isEmpty();

	boolean remove(Object o);

	boolean removeAll(Collection2<?> c);

	boolean retainAll(Collection2<?> c);

	int size();

	Object[] toArray();

	<T> T[] toArray(T[] a);
}

interface List2<E> extends Collection2<E> {

	boolean add(int i, E e);

}

interface Queue2<E> extends Collection2<E> {

	E element();

	boolean offer(E o);

	E peek();

	E poll();

	E remove();

}

abstract class AbstractCollection<E> implements Collection2<E> {

	@Override
	public boolean add(E o) {
		return false;
	}

	public Iterator<E> iterator() {
		return null;
	}

}

class ArrayList<E> extends AbstractCollection<E> implements List2<E> {

	@Override
	public boolean add(E o) {
		return super.add(o);
	}

	public boolean add(int i, E o) {
		return true;
	}

	@Override
	public boolean addAll(Collection2<? extends E> c) {
		return false;
	}

	@Override
	public void clear() {
	}

	@Override
	public boolean contains(Object o) {
		return false;
	}

	@Override
	public boolean containsAll(Collection2<?> c) {
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean remove(Object o) {
		return false;
	}

	@Override
	public boolean removeAll(Collection2<?> c) {
		return false;
	}

	@Override
	public boolean retainAll(Collection2<?> c) {
		return false;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Object[] toArray() {
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return null;
	}
}

interface Set2<E> extends Collection2<E> {

	@Override
	boolean add(E o);

	@Override
	boolean addAll(Collection2<? extends E> c);

	@Override
	void clear();

	@Override
	boolean contains(Object o);

	@Override
	boolean containsAll(Collection2<?> c);

	@Override
	boolean equals(Object o);

	@Override
	int hashCode();

	@Override
	boolean isEmpty();

	@Override
	boolean remove(Object o);

	@Override
	boolean removeAll(Collection2<?> c);

	@Override
	boolean retainAll(Collection2<?> c);

	@Override
	int size();

	@Override
	Object[] toArray();

	@Override
	<T> T[] toArray(T[] a);

}

abstract class AbstractSet2<E> extends AbstractCollection<E> implements Set2<E> {

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean removeAll(Collection2<?> c) {

		int size = size();
		if (size < c.size()) {
			// If the member of 'this' is in 'c', remove it from 'this'.
			//
		} else {
			// Remove every member of 'c' from 'this'.
			//
		}
		return (size != size());
	}

}

class ConcreteSet2<E> extends AbstractSet2<E> {

	@Override
	public boolean add(E o) {
		return super.add(o);
	}

	@Override
	public boolean addAll(Collection2<? extends E> c) {
		return false;
	}

	@Override
	public void clear() {
	}

	@Override
	public boolean contains(Object o) {
		return false;
	}

	@Override
	public boolean containsAll(Collection2<?> c) {
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	public Iterator<E> iterator() {
		return super.iterator();
	}

	@Override
	public boolean remove(Object o) {
		return false;
	}

	@Override
	public boolean retainAll(Collection2<?> c) {
		return false;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Object[] toArray() {
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return null;
	}
}
