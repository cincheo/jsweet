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
	public E getOperator() {
		return E.A;
	}
}

abstract class AbstractSub extends Root {
	protected final List<Root> clauses;

	AbstractSub(final Root... params) {
		super();
		this.clauses = Arrays.asList(params);
	}

}

abstract class Root {
	abstract E getOperator();
}
