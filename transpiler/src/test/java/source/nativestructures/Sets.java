package source.nativestructures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Sets {

	public static void main(String[] args) {
		Map<Set, String> map = new HashMap<>();
		Set s = new HashSet();
		s.add("1");
		s.add("2");
		Set s2 = new HashSet();
		s2.add("2");
		s2.add("1");
		map.put(s, "hi");
		map.put(s2, "bye");

		System.out.println(map.get(s));
		// TODO: make this work (see #196)
		// assert "bye".equals(map.get(s));
	}

}
