package source.enums;

import source.enums.other.EnumWrapper;

public class SwitchWithEnumWrapper {

	public static void main(String[] args) {
		assert test(new EnumWrapper()).equals("V1");
	}

	public static String test(EnumWrapper wrapper) {
		switch(wrapper.getEnum()) {
		case V1:
			return "V1";
		case V2:
			return "V2";
		}
		return "UNMATCHED";
	}

}