package java.util;

public class Hashtable<K, V> extends HashMap<K, V> implements Dictionary<K, V> {

	private static final long serialVersionUID = 1L;

	public Hashtable() {
		super();
	}

	public Hashtable(int ignored, float alsoIgnored) {
		super(ignored, alsoIgnored);
	}

	public Hashtable(int ignored) {
		super(ignored);
	}

	public Hashtable(Map<? extends K, ? extends V> toBeCopied) {
		super(toBeCopied);
	}

	public Enumeration<K> keys() {
		final Iterator<K> it = keySet().iterator();
		return new Enumeration<K>() {
			@Override
			public boolean hasMoreElements() {
				return it.hasNext();
			}

			@Override
			public K nextElement() {
				return it.next();
			}
		};
	}

	public Enumeration<V> elements() {
		final Iterator<V> it = values().iterator();
		return new Enumeration<V>() {
			@Override
			public boolean hasMoreElements() {
				return it.hasNext();
			}

			@Override
			public V nextElement() {
				return it.next();
			}
		};
	}

}
