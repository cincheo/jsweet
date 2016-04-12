package source.generics;

import java.util.function.Consumer;

import jsweet.dom.Event;
import jsweet.dom.XMLHttpRequest;

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

}