package source.enums;

import javax.crypto.spec.DESedeKeySpec;

public enum ErasedEnum {
	A, B, C;

	void unsupportedMethod(DESedeKeySpec spec) {
	}

}