/* 
 * JSweet - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package source.typing;

import static jsweet.util.Lang.$export;

import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import def.dom.Event;
import def.dom.EventListener;
import def.js.Array;
import jsweet.util.function.TriFunction;

class BrowserEventListener {
	public String eventType;
	public EventListener func;
	public BrowserEventListener (String eventType, Function<Event,Boolean> theFunc) {
		this.eventType = eventType;
		this.func = new EventListener () {
			public void $apply (Event e) {
				theFunc.apply(e);
			}
		};
	}
}

@FunctionalInterface
interface FI {
	int m(String s);
}

class CFI implements FI {
	public int m(String s) {
		Lambdas.trace.push("1:" + s);
		return 1;
	}
}

public class Lambdas<T> {

	public Lambdas() {
		i = 1;
	}

	public Lambdas(int i) {
		this.i = i;
	}
	
	int i;

	public static Array<String> trace = new Array<>();

	final BinaryOperator<T>[] operations = null;
	final Lambdas<UnaryOperator<T>> unary = null;

	FI fi2 = new FI() {
		@Override
		public int m(String s) {
			Lambdas.trace.push("2:" + s);
			return 2;
		}
	};

	void m() {
		Runnable r = (Runnable) () -> {
		};
		r.run();
	}

	static void accept(FI fi) {
		fi.m("0");
	}

	void test(Function<String, String> f) {
		$export("result", f.apply("a"));
	}

	public static void main(String[] args) {
		new Lambdas<String>().test(p -> p);
		accept(new CFI());
		new CFI().m("1");
		FI fi = new FI() {
			@Override
			public int m(String s) {
				Lambdas.trace.push("2:" + s);
				return 2;
			}
		};
		fi.m("2");
		accept(new FI() {
			@Override
			public int m(String s) {
				Lambdas.trace.push("3:" + s);
				return 3;
			}
		});
		$export("trace", trace.join(","));
	}

	void invoker() {
		f1.apply("a", "b");
		f2.apply("a", "b", "c");
		f3.getAsDouble();
		f4.accept(20);
		f5.accept("a", "b");
	}

	Callable<Boolean> getCallable() {
		return new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return true;
			}
		};
	}
	
	Callable<Boolean> c1 = () -> false;
	
	BiFunction<String, String, Boolean> f1;

	TriFunction<String, String, String, Boolean> f2;

	DoubleSupplier f3;

	DoubleConsumer f4;

	BiConsumer<String, String> f5;

}
