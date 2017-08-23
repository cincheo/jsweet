package source.enums;

import static jsweet.util.Lang.$export;
import static jsweet.util.Lang.$insert;

import def.js.Array;

public class ComplexEnums {
	static Array<String> trace = new Array<String>();

	public static void main(String[] args) {
		MyComplexEnum.aStaticMethod();
		assert !MyComplexEnum.RATIO_16_9.equals(MyComplexEnum.RATIO_2_1);
		assert MyComplexEnum.RATIO_16_9.equals(MyComplexEnum.RATIO_16_9);
		trace.push("" + MyComplexEnum.RATIO_2_1.getValue());
		trace.push("" + MyComplexEnum.RATIO_2_1.str);
		trace.push("" + MyComplexEnum.RATIO_2_1.otherName);
		trace.push("" + (MyComplexEnum.RATIO_16_9 == MyComplexEnum.RATIO_16_9));
		trace.push("" + (MyComplexEnum.RATIO_16_9.name() == MyComplexEnum.RATIO_16_9.name()));
		trace.push("" + (MyComplexEnum.RATIO_16_9.ordinal() == MyComplexEnum.RATIO_16_9.ordinal()));
		trace.push("" + (MyComplexEnum.RATIO_16_9 != (MyComplexEnum) MyComplexEnum.RATIO_3_2));
		assert A.i == 2;
		assert $insert("ComplexEnums.A['__class']") == "source.enums.ComplexEnums.A";
		assert $insert("ComplexEnums.InnerEnum['__class']") == "source.enums.ComplexEnums.InnerEnum";
		trace.push("" + InnerEnum.E3.getMode());
		trace.push("" + A.i);

		$export("trace", ">" + trace.join(","));
	}

	public static class A {
		public static int i = 2;
	}

	public static enum InnerEnum {

		E1(0x0),

		E2(0x1),

		E3(0x2),

		E4(0x3);

		int mode;

		private InnerEnum(int mode) {
			this.mode = mode;
		}

		public int getMode() {
			return mode;
		}

	}
}

/** 
 * JavaDoc test 
 */
enum MyComplexEnum {
	FREE_RATIO(null), VIEW_3D_RATIO(null), RATIO_4_3(4f / 3), RATIO_3_2(1.5f), RATIO_16_9(16f / 9), RATIO_2_1(
			2f / 1f), SQUARE_RATIO(1f);

	private final Float value;

	public String str;

	public String otherName;

	private MyComplexEnum(Float value) {
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
		ComplexEnums.trace.push("static");
	}

	public static void aStaticMethod2() {
	}

	public void aNonStaticMethod() {
	}

}

enum Mode {

	/**
	 * Disable all Pull-to-Refresh gesture and Refreshing handling
	 */
	DISABLED(0x0),

	/**
	 * Only allow the user to Pull from the start of the Refreshable View to
	 * refresh. The start is either the Top or Left, depending on the scrolling
	 * direction.
	 */
	PULL_FROM_START(0x1),

	/**
	 * Only allow the user to Pull from the end of the Refreshable View to
	 * refresh. The start is either the Bottom or Right, depending on the
	 * scrolling direction.
	 */
	PULL_FROM_END(0x2),

	/**
	 * Allow the user to both Pull from the start, from the end to refresh.
	 */
	BOTH(0x3);

	private Mode(int mode) {
	}

}