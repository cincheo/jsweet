package source.structural;

public class InheritanceOrderInSameFile extends ParentClass {

	public static interface ParentInterface {
	}

	public static abstract class AbstractParent implements ParentInterface {

		private final class CharSequenceIterator extends AbstractCharIterator {
		}
	}

	public static class Child extends AbstractParent {

		private final class CharSequenceIterator extends AbstractCharIterator {
		}
	}

	public static abstract class AbstractCharIterator implements CharIterator {
	}

	public static interface CharIterator {
	}

}

class ParentClass {

}