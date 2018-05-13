package source.overload;

import static jsweet.util.Lang.$export;

import def.js.Array;

public class OverLoadWithClassParam {

	static Array<String> trace = new Array<>();

	OverLoadWithClassParam(Class clazz) {
		this(clazz, 0);
	}

	OverLoadWithClassParam(Class clazz, int p1) {
		this(clazz, p1, 0);
	}

	OverLoadWithClassParam(Class clazz, int p1, int p2) {
		trace.push("ctor_overload_class;" + clazz.getSimpleName() + ";" + p1 + ";" + p2);
	}

	void m(Class clazz) {
		m(clazz, 0);
	}

	void m(Class clazz, int p1) {
		m(clazz, p1, 0);
	}

	void m(Class clazz, int p1, int p2) {
		trace.push("m_overload_class;" + clazz.getSimpleName() + ";" + p1 + ";" + p2);
	}

	public static void main(String[] args) {
		new OverLoadWithClassParam(OverLoadWithClassParam.class);
		new OverLoadWithClassParam(OverLoadWithClassParam.class, 4);
		OverLoadWithClassParam o = new OverLoadWithClassParam(OverLoadWithClassParam.class, 10, 100);

		o.m(OverLoadWithClassParam.class);
		o.m(OverLoadWithClassParam.class, 4);
		o.m(OverLoadWithClassParam.class, 10, 100);

		$export("trace", trace.join(","));
	}

}
