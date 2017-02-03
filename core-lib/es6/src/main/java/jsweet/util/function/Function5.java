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
package jsweet.util.function;

/**
 * Represents a function that accepts 5 arguments and produces a result.
 */
public interface Function5<T1, T2, T3, T4, T5, R> {

	/**
	 * Applies this function to the given arguments.
	 */
	R apply(T1 p1, T2 p2, T3 p3, T4 p4, T5 p5);

}
