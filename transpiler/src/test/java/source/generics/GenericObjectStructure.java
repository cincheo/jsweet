package source.generics;

import java.util.function.Consumer;

import jsweet.lang.Interface;

public class GenericObjectStructure {

	<T> void m(MyInterface<T> i, Consumer<T> c) {

	}

	public static void main(String[] args) {

		new GenericObjectStructure().m(new MyInterface<String>() {
			{
				f = "test";
			}
		}, param -> { param.indexOf("c"); });

	}

}

@Interface
abstract class MyInterface<T> {
	String f;
}
