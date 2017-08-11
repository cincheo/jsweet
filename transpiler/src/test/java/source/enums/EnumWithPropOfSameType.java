package source.enums;

public enum EnumWithPropOfSameType {
	A, B, C;

	static {
		A.next = B;
		B.next = C;
		C.next = A;
	}

	public EnumWithPropOfSameType next;

	EnumWithPropOfSameType() {
	}

}
