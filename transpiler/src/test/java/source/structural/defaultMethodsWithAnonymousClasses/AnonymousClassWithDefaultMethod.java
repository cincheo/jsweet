package source.structural.defaultMethodsWithAnonymousClasses;

interface MyIterator<K> {
	default boolean hasNext() {
		return false;
	}

	default K next() {
		return null;
	}

	default void remove() {
	}

}

interface MyEntry<K,V> {
	default K getKey() {
		return null;
	}
}

public class AnonymousClassWithDefaultMethod<K, V> {

	public MyIterator<K> iterator() {
		final MyIterator<MyEntry<K, V>> entryIterator = null;
		return new MyIterator<K>() {
			@Override
			public boolean hasNext() {
				return entryIterator.hasNext();
			}

			@Override
			public K next() {
				MyEntry<K, V> entry = entryIterator.next();
				return entry.getKey();
			}

			@Override
			public void remove() {
				entryIterator.remove();
			}
		};
	}

}
