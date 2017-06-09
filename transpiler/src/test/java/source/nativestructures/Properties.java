package source.nativestructures;

import static jsweet.util.Lang.$export;

import java.util.Collections;
import java.util.Map.Entry;

import def.js.Array;

/**
 * This test is executed without any Java runtime.
 */
public class Properties {

	static Array<String> trace = new Array<>();

	static String key1() {
		return "1";
	}

	static String key2() {
		return "a";
	}

	public static void main(String[] args) {
		java.util.Properties p = new java.util.Properties();

		p.put(key1(), "a");
		p.put("2", "b");

		for(Entry<Object, Object> e : p.entrySet()) {
			trace.push("" + e.getKey());
			trace.push("" + e.getValue());
		}
		
		trace.push("" + p.size());
		trace.push(p.get("1").toString());

		trace.push("" + p.containsKey("2"));

		trace.push(p.keySet().toString());
		trace.push(p.stringPropertyNames().toString());
		trace.push("isEmpty=" + p.isEmpty());

		trace.push("" + p.values());

		p.remove("1");

		trace.push("" + p.size());
		trace.push("" + (p.get("1") == null));

		trace.push(Collections.singletonMap(key2(), "1").get("a"));
		trace.push(Collections.singletonMap("b", "2").get("b"));

		p.remove(key1());
		trace.push("size=" + p.size());
		trace.push("unknown=" + p.getProperty(key1()));
		
		p.setProperty(key1(), "a_again");
		trace.push("size=" + p.size());
		 
		java.util.Properties p2 = (java.util.Properties) p.clone();
		
		p.clear();
		trace.push(p.values().toString());
		trace.push("size=" + p.size());
		trace.push("isEmpty=" + p.isEmpty());
		trace.push("size2=" + p2.size());
		trace.push("isEmpty2=" + p2.isEmpty());

		trace.push("-" + p.get("undefinedKey") + "-");
		
		$export("trace", trace.join(","));

	}

}
