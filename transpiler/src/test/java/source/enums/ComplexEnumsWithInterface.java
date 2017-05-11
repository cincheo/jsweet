package source.enums;

public class ComplexEnumsWithInterface {
    public static void main(String[] args) {
	DayOfWeek day = DayOfWeek.Wednesday;
	assert day.persistenceValue == 3;
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