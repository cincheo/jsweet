package source.overload;

import java.util.List;

public class LocalVariablesNameCollision {
	public void foo() {
	}

	public void foo(List<Item> items) {
		for (Item item : items) {
		}

		Item item = items.get(0);
	}
}

class Item {
}

class AssertionError extends Error {

	public AssertionError() {
	}

	public AssertionError(boolean message) {
		this(String.valueOf(message));
	}

	private AssertionError(String message) {
		super(message);
	}
}
