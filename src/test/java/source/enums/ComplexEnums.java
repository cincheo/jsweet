package source.enums;

import static jsweet.util.Globals.$export;

import jsweet.lang.Array;

public class ComplexEnums {
	static Array<String> trace = new Array<String>();

	public static void main(String[] args) {
		trace.push("" + MyComplexEnum.RATIO_2_1.getValue());
		trace.push("" + MyComplexEnum.RATIO_2_1.str);
		trace.push("" + MyComplexEnum.RATIO_2_1.otherName);
		trace.push("" + (MyComplexEnum.RATIO_16_9 == MyComplexEnum.RATIO_16_9));
		trace.push("" + (MyComplexEnum.RATIO_16_9.name() == MyComplexEnum.RATIO_16_9.name()));
		trace.push("" + (MyComplexEnum.RATIO_16_9.ordinal() == MyComplexEnum.RATIO_16_9.ordinal()));
		trace.push("" + (MyComplexEnum.RATIO_16_9 != MyComplexEnum.RATIO_3_2));
		$export("trace", ">" + trace.join(","));
	}
}

enum MyComplexEnum {
	FREE_RATIO(null), VIEW_3D_RATIO(null), RATIO_4_3(4f / 3), RATIO_3_2(1.5f), RATIO_16_9(16f / 9), RATIO_2_1(2f / 1f), SQUARE_RATIO(1f);

	private final Float value;

	public String str;

	public String otherName;

	private MyComplexEnum(Float value) {
		this.value = value;
		this.str = "--" + value.intValue() + "--";
		this.otherName = this.name().toLowerCase() + "_" + ordinal();
	}

	public Float getValue() {
		return value;
	}

}
