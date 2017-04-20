package source.overload;

import static jsweet.util.Lang.$export;

import def.js.Array;

public abstract class BasicOverride<T extends BasicData> {

	static Array<String> trace = new Array<String>();

	public static void main(String[] args) {
		new BasicOverride1().test1(new Data1());
		new BasicOverride1().test1(null);
		new BasicOverride1().test2(new Data1());
		new BasicOverride1().test2(null);
		new BasicOverride1().test3(new Data1());
		new BasicOverride1().test3(null);
		new BasicOverride2().test1(new Data2());
		new BasicOverride2().test1(null);
		new BasicOverride2().test2(new Data1());
		new BasicOverride2().test2(null);
		new BasicOverride2().test3(new Data2());
		new BasicOverride2().test3(null);
		new BasicOverride2().test3(new Data1());
		$export("trace", trace.join(","));
	}

	public abstract void test1(T t);

	public abstract void test2(BasicData d);

	public void test3(BasicData d) {
		trace.push(d == null ? "0-3-0" : "0-3-X");
	}

}

class BasicOverride1 extends BasicOverride<Data1> {

	@Override
	public void test1(Data1 t) {
		trace.push(t == null ? "1-1-0" : "1-1-X");
	}

	@Override
	public void test2(BasicData d) {
		trace.push(d == null ? "1-2-0" : "1-2-X");
	}

	public void test3(Data1 d) {
		trace.push(d == null ? "1-3-0" : "1-3-X");
	}

}

class BasicOverride2 extends BasicOverride<Data2> {

	@Override
	public void test1(Data2 t) {
		trace.push(t == null ? "2-1-0" : "2-1-X");
	}

	@Override
	public void test2(BasicData d) {
		trace.push(d == null ? "2-2-0" : "2-2-X");
	}

	public void test3(Data2 d) {
		trace.push(d == null ? "2-3-0" : "2-3-X");
	}
}

class BasicData {

}

class Data1 extends BasicData {

}

class Data2 extends BasicData {

}