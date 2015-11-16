/* Copyright 2015 CINCHEO SAS
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
package org.jsweet.test.transpiler.source.typing;

import static jsweet.util.Globals.array;
import jsweet.lang.Array;
import java.util.function.Consumer;

public class ArraysOfLambdas {

	Consumer<Object>[] array = array(new Array<Consumer<Object>>());

	public void a() {
		doItInfinitely(runs);
	}

	java.lang.Runnable[] runs;

	@SuppressWarnings("unused")
	Runnable[] doItInfinitely(Runnable[] runs) {
		Runnable[] runsTemp = runs;

		Array<Runnable> runsTemp2 = array(runsTemp);
		
		Array<java.lang.Runnable> runsTemp3 = runsTemp2;
		
		runsTemp2.push(() -> { /* ... */ });
		
		return runsTemp;
	}
}
