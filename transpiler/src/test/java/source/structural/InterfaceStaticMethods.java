package source.structural;

import source.structural.globalclasses.a.AbstractClass;
import source.structural.globalclasses.a.InterfaceWithStaticMethod;

public class InterfaceStaticMethods<U> extends AbstractClass {

	public static void main(String[] args) {
		InterfaceWithStaticMethod.aStaticMethod();
		InterfaceWithStaticMethod.overloadedStaticMethod("abc");
		InterfaceWithStaticMethod.overloadedStaticMethod(123);
	}

}
