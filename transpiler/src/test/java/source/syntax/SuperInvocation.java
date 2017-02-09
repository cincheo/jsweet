package source.syntax;

class SuperClass extends def.js.Object {

	public SuperClass() {
	}

	public SuperClass(String param) {
	}

}

public class SuperInvocation extends SuperClass {

	public SuperInvocation(String param) {
		System.out.println("first statement");
		$super("test");
	}

}
