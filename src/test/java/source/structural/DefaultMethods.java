package source.structural;

import static jsweet.util.Globals.$export;

public class DefaultMethods implements IDefaultMethods {

	public static void main(String[] args) {
		new DefaultMethods().m();
	}
	
}

interface IDefaultMethods {
	
	default void m() {
		$export("value", "test");
	}
	
}
