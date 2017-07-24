package source.reflection;

import java.lang.reflect.Array;

public class ArrayMethods {

	public void work() {

		Object values = new String[] { "0", "1" };

		int size = Array.getLength(values);

		assert size == 2;

		Object first = Array.get(values, 0);

		assert "0".equals(first);

		Array.set(values, 0, "2");

		assert "0".equals(Array.get(values, 0));

	}

}