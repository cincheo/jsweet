package source.generics;

public abstract class InferenceWithClassParameter {

	public void use() {
		I2 i2 = create(I2.class);
		i2.m();
	}

	public abstract <TYPE extends I1> TYPE create(Class<TYPE> type);

}

interface I1 {

}

interface I2 extends I1 {

	void m();

}

interface Factory {

	<TYPE extends I1> TYPE create(Class<TYPE> type);

	default I2 createButton(String label, Runnable onClick) {
		I2 i2 = create(I2.class);
		i2.m();
		return i2;
	}
}

class MyFactory implements Factory {

	@Override
	public <TYPE extends I1> TYPE create(Class<TYPE> type) {
		return null;
	}

}
