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
package source.throwable;

import jsweet.lang.Error;
import static jsweet.util.Globals.$export;

public class TryCatchFinallyTest {

	static void m() {
		$export("executed", true);
		throw new Error("test-message");
	}

}

class Globals {

	public static void main(String[] args) {
		try {
			TryCatchFinallyTest.m();
		} catch (Error e) {
			$export("message", e.message);
			return;
		} finally {
			$export("finally_executed", true);
		}
	}

}
