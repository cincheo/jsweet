package source.nativestructures;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ES6Maps {
	public static void main(String[] args) {
		// put / clear
		Map<String, Integer> m1 = new HashMap<>();
		assert m1.size() == 0;
		Integer prev = m1.put("1", 1);
		assert prev == null;
		assert !m1.isEmpty();

		Integer prev2 = m1.put("1", 2);
		assert prev2 == 1;
		assert m1.size() == 1;

		// clear
		m1.clear();
		assert m1.size() == 0;
		assert m1.isEmpty();

		// containsKey / containsValue / remove
		m1.put("1", 2);
		boolean containsKey1 = m1.containsKey("1");
		assert containsKey1;
		boolean containsValue1 = m1.containsValue(2);
		assert containsValue1;
		Integer removed = m1.remove("1");
		assert removed == 2;

		boolean containsKey2 = m1.containsKey("1");
		assert !containsKey2;
		boolean containsValue2 = m1.containsValue(2);
		assert !containsValue2;

		// putAll
		Map<String, Integer> m2 = new HashMap<>();
		m2.put("1", 2);
		m2.put("2", 4);
		m1.putAll(m2);
		assert m1.size() == 2;
		assert m1.get("1") == 2;
		assert m1.get("2") == 4;

		// entrySet
		Set<Entry<String, Integer>> entrySet = m2.entrySet();
		assert entrySet.size() == 2;
		boolean firstFound = false;
		boolean secondFound = false;
		for (Entry<String, Integer> e : entrySet) {
			if (e.getKey() == "1" && e.getValue() == 2) {
				firstFound = true;
			} else if (e.getKey() == "2" && e.getValue() == 4) {
				secondFound = true;
			}
		}

		assert firstFound;
		assert secondFound;

		// new HashMap(Collection<>);
		Map<String, Integer> m3 = new HashMap<>(m2);
		assert m3.size() == 2;
		assert m3.get("1") == 2;
		assert m3.get("2") == 4;

		// new HashMap(int initialCapacity);
		Map<String, Integer> m4 = new HashMap<>(15);
		assert m4.size() == 0;
		assert m4.isEmpty();

		// new HashMap(int initialCapacity, float loadFactor);
		m4 = new HashMap<>(15, 89);
		assert m4.size() == 0;
		assert m4.isEmpty();
		
		/**
		 * test some of the default methods
		 */
		Map<String, Integer> m9 = new HashMap<>();
		m9.put("k1", 1);
		m9.compute("k1", (k, v) -> v * 2);
		assert m9.get("k1") == 2;
		
		m9.put("k1", null);
		m9.computeIfAbsent("k1", (k) -> 3);
		m9.computeIfAbsent("k2", (k) -> 3);
		assert m9.get("k1") == 3;
		assert m9.get("k2") == 3;
		
		m9.put("k1", null);
		m9.put("k2", 2);
		m9.computeIfPresent("k1", (k, v) -> v * 2);
		m9.computeIfPresent("k2", (k, v) -> v * 2);
		assert m9.get("k1") == null;
		assert m9.get("k2") == 4;
		
		m9.put("k1", null);
		m9.put("k2", 2);
		Integer vk1 = m9.getOrDefault("k1", 7);
		Integer vk2 = m9.getOrDefault("k2", 7);
		assert vk1 == 7;
		assert vk2 == 2;
	}
}
