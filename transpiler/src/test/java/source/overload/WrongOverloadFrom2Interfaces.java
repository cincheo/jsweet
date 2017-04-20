package source.overload;

import static jsweet.util.Lang.$export;

import def.js.Array;

public class WrongOverloadFrom2Interfaces {

	public static Array<String> trace = new Array<String>();

	public static void main(String[] args) {
		UnmodifiableList<String> l = new UnmodifiableList<String>();
		l.remove("abc");
		l.remove(1);
		$export("trace", trace.join(","));
	}

}

interface Collection<T> {
	boolean remove(Object arg);
}

interface List<T> {
	T remove(int arg);
}

class UnmodifiableCollection<T> implements Collection<T> {
	@Override
	public boolean remove(Object arg) {
		WrongOverloadFrom2Interfaces.trace.push("remove1: " + arg);
		return false;
	}
}

class UnmodifiableList<T> extends UnmodifiableCollection<T> implements List<T> {
	@Override
	public T remove(int arg) {
		WrongOverloadFrom2Interfaces.trace.push("remove2: " + arg);
		return null;
	}
}
