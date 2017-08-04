package source.enums;

import source.enums.other.ComplexEnumsAccess;

public enum MyComplexEnum2 {
	FREE_RATIO(null), VIEW_3D_RATIO(null), RATIO_4_3(4f / 3), RATIO_3_2(1.5f), RATIO_16_9(16f / 9), RATIO_2_1(
			2f / 1f), SQUARE_RATIO(1f);

	private final Float value;

	public String str;

	public String otherName;

	private MyComplexEnum2(Float value) {
		this.value = value;
		this.str = "--" + value.intValue() + "--";
		this.otherName = this.name().toLowerCase() + "_" + ordinal();
		aNonStaticMethod();
		this.aNonStaticMethod();
		aStaticMethod2();
	}

	public Float getValue() {
		aNonStaticMethod();
		this.aNonStaticMethod();
		aStaticMethod2();
		return value;
	}

	public static void aStaticMethod() {
		ComplexEnumsAccess.trace2.push("static");
	}

	public static void aStaticMethod2() {
	}

	public void aNonStaticMethod() {
	}

}
