package source.overload;

public abstract class BasicOverride<T extends BasicData> {

	public abstract void test1(T t);
	
	public abstract void test2(BasicData d);
	
	public void test3(BasicData d) {
		
	}
	
}

class BasicOverride1 extends BasicOverride<Data1> {

	@Override
	public void test1(Data1 t) {
	}

	@Override
	public void test2(BasicData d) {
	}

	public void test3(Data1 d) {
	}
	
}

class BasicOverride2 extends BasicOverride<Data2> {

	@Override
	public void test1(Data2 t) {
	}

	@Override
	public void test2(BasicData d) {
	}
	
	public void test3(Data2 d) {
	}
}

class BasicData {

}

class Data1 extends BasicData {

}

class Data2 extends BasicData {

}