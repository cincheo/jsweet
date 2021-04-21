package source.nativestructures;

import java.util.HashMap;

public class MapExtended extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean containsKey(Object key) {
		return get(key) != null;
	}

	@Override
	public Object get(Object key) {
		Object o = super.get(key);
		if ("null".equals(o))
			return null;
		return o;
	}

}