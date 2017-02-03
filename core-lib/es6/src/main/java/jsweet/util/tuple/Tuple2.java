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
package jsweet.util.tuple;

/**
 * This class allows to simulate tuples of 2 elements, tuple types representing
 * arrays with individually tracked element types.
 * 
 * @author Renaud Pawlak
 *
 * @param <T0> the type of the 0-indexed element
 * @param <T1> the type of the 1-indexed element
 */
public class Tuple2<T0, T1> {

	/**
	 * The 0-indexed element.
	 */
	public T0 $0;
	/**
	 * The 1-indexed element.
	 */
	public T1 $1;

	/**
	 * Creates a 2-element tuple.
	 * @param $0 the 0-indexed element
	 * @param $1 the 1-indexed element
	 */
	public Tuple2(T0 $0, T1 $1) {
		super();
		this.$0 = $0;
		this.$1 = $1;
	}

}
