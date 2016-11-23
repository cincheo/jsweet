package source.structural;

import source.structural.InnerClass.InnerClass2;
import source.structural.other.Wrapping.Inner;

public class InnerClassUse {

	public void usingMethodOverloaded(Inner inner) {
		// ...
	}

	public void usingMethodOverloaded(InnerClass2 inner) {
		// ...
	}

	public void usingMethod1(Inner inner) {
		// ...
	}

	public void usingMethod2(InnerClass2 inner) {
		// ...
	}

	public void usingMethod3(InnerClass.InnerClass2 inner) {
		// ...
	}

}
