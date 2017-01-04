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

import static def.dom.Globals.console;

import java.util.function.Function;

public class Numbers<T> {

	Point2D.Float m() {
		return new Point2D.Float();
	}

	void convertStats(Function<Double[], Integer[]> transform) {
		Double[] stats = new Double[] { 1.0, 2.0 };
		Integer[] converted = transform.apply(stats);
		console.log(converted);
	}

	void arraysOfNumbers(Double[] d, Integer[] i, double[] d2, int[] i2) {
	}

	void numbers(Double d, Integer i, double d2, int i2, Float f, float f2) {
		f = 0.2f;
		@SuppressWarnings("unused")
		Numbers<Double[]> numbers = null;
	}

	Double[] getDoubles() {
		return new Double[] { 1.0, 2.0, 3.0 };
	}

	Float[] getFloats() {
		return new Float[] { 1.0f, 2.0f, 3.0f };
	}

	Integer[] getInts() {
		return new Integer[] { 1, 2, 3 };
	}

	double[] getPrimDoubles() {
		return new double[] { 1.0, 2.0, 3.0 };
	}

	float[] getPrimFloats() {
		return new float[] { 1.0f, 2.0f, 3.0f };
	}

	int[] getPrimInts() {
		return new int[] { 1, 2, 3 };
	}

	Double getDouble() {
		return 1.0;
	}

	Float getFloat() {
		return 1.0f;
	}

	Long getLong() {
		return 1L;
	}

	Integer getInt() {
		return 1;
	}

	double getPrimDouble() {
		return 1.0;
	}

	float getPrimFloat() {
		return 1.0f;
	}

	int getPrimInt() {
		return 1;
	}
}

class Point2D {

	static class Float extends Point2D {

	}

}
