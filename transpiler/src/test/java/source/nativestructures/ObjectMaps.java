package source.nativestructures;

import static jsweet.util.Lang.$export;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import def.js.Array;

class MyKey {
	String data;
	public MyKey(String data) {
		this.data = data;
	}
	public String toString() {
		return data;
	}
	@Override
	public boolean equals(Object obj) {
		return data.equals(((MyKey)obj).data);
	}
}

/**
 * This test is executed without any Java runtime.
 */
public class ObjectMaps {

	static Array<String> trace = new Array<>();

	static MyKey key1() {
		return new MyKey("1");
	}

	static MyKey key2() {
		return new MyKey("a");
	}

	public static void main(String[] args) {
		Map<MyKey, String> m = new HashMap<>();

		m.put(key1(), "a");
		m.put(new MyKey("2"), "b");

		for(Entry<MyKey, String> e : m.entrySet()) {
			trace.push("" + e.getKey());
			trace.push("" + e.getValue());
		}
		
		trace.push("" + m.size());
		trace.push(m.get(new MyKey("1")));

		trace.push("" + m.containsKey(new MyKey("2")));

		trace.push("" + m.keySet());

		trace.push("" + m.values());

		m.remove(new MyKey("1"));

		trace.push("" + m.size());
		trace.push("" + (m.get(new MyKey("1")) == null));

		trace.push(Collections.singletonMap(key2(), "1").get(new MyKey("a")));
		trace.push(Collections.singletonMap(new MyKey("b"), "2").get(new MyKey("b")));

		m.clear();
		trace.push("" + m.values());

		trace.push("-" + m.get(new MyKey("undefinedKey")) + "-");
		
		$export("trace", trace.join(","));

	}

}
