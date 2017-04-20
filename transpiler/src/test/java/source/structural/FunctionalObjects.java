package source.structural;

import static jsweet.util.Lang.$export;

import java.util.function.Consumer;

public class FunctionalObjects {

	public static void main(String[] args) {
		new Foo2().bar();
		f(s -> {
			$export("s1", s);
		}, "a");
		f(new Foo3(), "b");
	}

	public static void f(Consumer<String> f, String s) {
		f.accept(s);
	}

}

class Thread {
	public Thread(Runnable runnable) {
		runnable.run();
	}
}

class Foo2 implements Runnable {
	public void bar() {
		Foo2 f = new Foo2();
		new Thread(f);
		new Thread(() -> $export("run2", true));
	}

	@Override
	public void run() {
		$export("run1", true);
	}
}

class Foo3 implements Consumer<String> {

	@Override
	public void accept(String s) {
		$export("s2", s);
	}

}