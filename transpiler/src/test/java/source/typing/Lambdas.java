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

import static jsweet.util.Globals.$export;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import def.js.Array;
import jsweet.util.function.TriFunction;

@FunctionalInterface
interface FI {
	int m(String s);
}

class CFI implements FI {
	public int m(String s) {
		Lambdas.trace.push("1:"+s);
		return 1;
	}
}

public class Lambdas<T> {

	public static Array<String> trace = new Array<>();
	
	final BinaryOperator<T>[] operations = null;
	final Lambdas<UnaryOperator<T>> unary = null;

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
		new FI() {
			@Override
			public int m(String s) {
				Lambdas.trace.push("2:"+s);
				return 2;
			}
		}.m("2");
		accept(new FI() {
			@Override
			public int m(String s) {
				Lambdas.trace.push("3:"+s);
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

	BiFunction<String, String, Boolean> f1;

	TriFunction<String, String, String, Boolean> f2;

	DoubleSupplier f3;

	DoubleConsumer f4;

	BiConsumer<String, String> f5;

}
