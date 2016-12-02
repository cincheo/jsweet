package source.root.root2;

import source.root.noroot.a.B;
import source.root.root2.a.A;

abstract class LocalAbstractClass {
	public abstract void m();
}

public class AccessFromClassInRoot {

	public static void main(String[] args) {
		// potential name clash
		A a = new A();
		new B();
		new LocalAbstractClass() {
			@Override
			public void m() {
			}
		};
	}
	
}

