package source.typing;

public class ClassType {
	public static Object ofType(final Class expected) {
		return null;
	}

	public void work() {
		Class clazz = Pred.class;
		Object predicate = ClassType.ofType(clazz);
	}
}

interface Pred {
	
}