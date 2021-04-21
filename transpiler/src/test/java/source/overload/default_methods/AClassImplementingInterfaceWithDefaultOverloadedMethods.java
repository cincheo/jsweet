package source.overload.default_methods;

public class AClassImplementingInterfaceWithDefaultOverloadedMethods
		implements AnInterfaceWithDefaultOverloadedMethods {

	public static void main(String[] args) {
		AClassImplementingInterfaceWithDefaultOverloadedMethods instance = new AClassImplementingInterfaceWithDefaultOverloadedMethods();
		assert instance.getNumber() == 1;
		assert instance.getNumber(10) == 20;
	}

}
