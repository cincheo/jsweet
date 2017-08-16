package source.varargs;

import java.util.Arrays;
import java.util.List;

public class VarargsOnConstructor {

}

enum E {
	A
}

class Sub extends AbstractSub {
	Sub(final Root... params) {
		super(params);
	}

	@Override
	public E m() {
		return E.A;
	}
}

abstract class AbstractSub extends Root {
	protected final List<Root> roots;

	AbstractSub(final Root... params) {
		super();
		this.roots = Arrays.asList(params);
	}

}

abstract class Root {
	abstract E m();
}
