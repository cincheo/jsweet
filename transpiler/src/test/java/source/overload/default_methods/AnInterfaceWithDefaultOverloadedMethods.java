package source.overload.default_methods;

public interface AnInterfaceWithDefaultOverloadedMethods {

	public default Integer getNumber() {
		System.out.println("overload1");
		return 1;
	}

	public default Integer getNumber(Integer unNombrePleinSignes) {
		System.out.println("overload2 " + unNombrePleinSignes);
		return unNombrePleinSignes + 10;
	}

}
