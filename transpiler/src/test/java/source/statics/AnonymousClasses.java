package source.statics;

import static jsweet.util.Lang.$export;

public class AnonymousClasses {

	public static void main(String[] args) {
		c.m();
	}

	abstract static class MyClass {

		public abstract void m();

	}

	static MyClass c = new MyClass() {
		@Override
		public void m() {
			$export("m", true);
		}
	};

}
