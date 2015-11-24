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
package source.tscomparison;

import static jsweet.util.Globals.$export;
import static jsweet.util.Globals.array;

class Operation {
	double x;
	public Operation(double x) {
		this.x = x; 
	}

	public double add(Double number) {
		return add(number, null, null);
	}

	public double add(Double number, Double d, Double[] ds) {
		return number + x;
	}
}

public class OtherThisExample {
	public static void main(String[] args) {
		double[] numbers = {1, 2, 3};
		Operation twoOperation = new Operation(2);
		Double[] results = array(numbers).map(twoOperation::add);
		$export("results", results);
	}
}