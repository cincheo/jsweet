package source.enums;

import java.util.HashMap;
import java.util.Map;

public class ComplexEnumsWithInterface {
	private final static Map<Integer, DayOfWeek> ORDINAL_MAP = new HashMap<>();

	public static DayOfWeek fromPersistenceValue(Integer value) {
		return ORDINAL_MAP.get(value);
	}

	public static void main(String[] args) {
		DayOfWeek day = DayOfWeek.Wednesday;
		assert day.persistenceValue == 3;
		for (DayOfWeek c : DayOfWeek.values()) {
			ORDINAL_MAP.put(c.persistenceValue, c);
		}
		assert ORDINAL_MAP.get(1) == DayOfWeek.Monday;
	}
	
	public void m(SomeInterface i) {
		
	}
}

enum DayOfWeek implements SomeInterface {
	Sunday(0), Monday(1), Tuesday(2), Wednesday(3), Thursday(4), Friday(5), Saturday(6);

	public final int persistenceValue;

	DayOfWeek(int persistenceValue) {
		this.persistenceValue = persistenceValue;
	}
}

interface SomeInterface {

}