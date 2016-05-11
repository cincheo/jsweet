package source.overload;

public class InterfaceInheritance {

}

interface Collection2<E> {

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

interface Queue2<E> extends Collection2<E> {

	E element();

	boolean offer(E o);

	E peek();

	E poll();

	E remove();

}
