package source.structural;

import java.util.HashMap;
import java.util.Map;

public class AnonymousInInterface {

	public static void main(String[] args) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("aaa", "bbb");
		ISomething something = ISomething.fromMap(map);
		assert something.getSomething("aaa") == "bbb";
	}

}

interface ISomething {
	<O> O getSomething(final String someId);

	static ISomething fromMap(final Map<String, Object> map) {
		return new ISomething() {
			@Override
			public <O> O getSomething(final String someId) {
				return (O) map.get(someId);
			}
		};
	}
}
