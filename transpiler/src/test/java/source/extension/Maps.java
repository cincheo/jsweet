package source.extension;

import java.util.HashMap;

import def.js.Array;

/**
 * This test is executed without any Java runtime.
 */
public class Maps {

	static Array<String> trace = new Array<>();

	static String key1() {
		return "1";
	}

	static String key2() {
		return "a";
	}

	public static void main(String[] args) {
		HashMap<String, String> m = new HashMap<>();

		m.put(key1(), "a");
		m.put("2", "b");

//		for(Entry<String, String> e : m.entrySet()) {
//			
//			trace.push("" + e.getKey());
//			trace.push("" + e.getValue());
//		}

		assert 2 == m.size();
		assert "a" == m.get("1");

		assert m.containsKey("2");

		assert m.keySet().toString() == "[1, 2]";

		assert m.values().toString() == "[a, b]";

	}

}
