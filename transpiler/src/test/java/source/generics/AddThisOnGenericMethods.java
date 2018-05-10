package source.generics;

import java.util.function.Consumer;

import def.dom.Event;
import def.dom.XMLHttpRequest;
import def.js.Promise;

public class AddThisOnGenericMethods<T> {

	T t;

	private <U> void m(AddThisOnGenericMethods<U> i) {
	}

	public <U> void m2(AddThisOnGenericMethods<U> i) {
		m(i);
		m7(t);
		m8(t);
		m9(3);
	}

	public void m3(AddThisOnGenericMethods<String> i) {
		m(i);
	}

	public void m4(AddThisOnGenericMethods<String> i) {
		m3(i);
	}

	public void m5(AddThisOnGenericMethods<T> i) {
	}

	public void m6(AddThisOnGenericMethods<T> i) {
		m5(i);
	}

	public void m7(T i) {
	}

	public <U> void m8(U i) {
	}

	public void m9(Number n) {
	}

	public static <V> void s1(AddThisOnGenericMethods<V> i) {
		s2(i);
	}

	public static <U> void s2(AddThisOnGenericMethods<U> i) {
	}

}

class Server {

	protected <T> void installXMLHttpRequestListeners(XMLHttpRequest xhr, Consumer<T> resolve, Consumer<Object> reject) {
		xhr.onload = (Event ev) -> {
			// both should work
			onXMLHttpRequestComplete(xhr, resolve, reject);
			this.<T> onXMLHttpRequestComplete(xhr, resolve, reject);
			return null;
		};
	}

	private <U> void onXMLHttpRequestComplete(XMLHttpRequest request, Consumer<U> onSuccessCallback, Consumer<Object> onErrorCallback) {
	}

	<T> Promise<String> get(T t) {
		return new Promise<String>((Consumer<String> c1, Consumer<Object> c2) -> {
		});
	};

}

class AbstractController<T> {

	protected void fillTable(T[] t) {
	}

}

class Controller extends AbstractController<String> {

	public void m() {
		new Server().get("abc").then(s -> {
			fillTable(new String[] { s });
		});
	}
}

interface SuperEntry<V> {

	V getValue();
	
	int m();
	
}

// test invocations on methods defined in an implemented interface
interface Entry<K, V> extends SuperEntry<V> {
	K getKey();

	V getValue();

	V setValue(V value);
	
	default void test() {
		
	}
}

abstract class AbstractMapEntry<K, V> implements Entry<K, V> {

	@Override
	public final boolean equals(Object other) {
		if (!(other instanceof Entry)) {
			return false;
		}
		Entry<?, ?> entry = (Entry<?, ?>) other;
		return Objects.equals(getKey(), entry.getKey()) && Objects.equals(getValue(), entry.getValue());
	}

}

class Objects {
	public static boolean equals(Object o1, Object o2) {
		return false;
	}
}
